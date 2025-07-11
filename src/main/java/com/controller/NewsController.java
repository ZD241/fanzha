
package com.controller;

import java.io.File;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import com.alibaba.fastjson.JSONObject;
import java.util.*;
import org.springframework.beans.BeanUtils;
import javax.servlet.http.HttpServletRequest;
import org.springframework.web.context.ContextLoader;
import javax.servlet.ServletContext;
import com.service.TokenService;
import com.utils.*;
import java.lang.reflect.InvocationTargetException;
import org.apache.commons.lang3.StringUtils;
import com.service.DictionaryService;
import javax.sql.DataSource;

import com.annotation.IgnoreAuth;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.entity.*;
import com.entity.view.*;
import com.service.*;
import com.utils.PageUtils;
import com.utils.R;
import com.alibaba.fastjson.*;

/**
 * 案例分析
 * 后端接口
 * @author
 * @email
*/
@RestController
@RequestMapping("/news")
public class NewsController {
    @Autowired
    private DataSource dataSource;

    // 测试数据库连接的方法
    @GetMapping("/test-db-connection")
    public R testDbConnection() {
        try(Connection connection=dataSource.getConnection()) {
            return R.ok("数据库连接成功");
        }catch (SQLException e) {
            return R.error("数据库连接失败"+e.getMessage());
        }
    }
   /* public R testDbConnection() {
        try (Connection connection = dataSource.getConnection()) {
            return R.ok("数据库连接成功");
        } catch (SQLException e) {
            return R.error("数据库连接失败: " + e.getMessage());
        }
    }*/
    private static final Logger logger = LoggerFactory.getLogger(NewsController.class);

    @Autowired
    private NewsService newsService;


    @Autowired
    private TokenService tokenService;
    @Autowired
    private DictionaryService dictionaryService;

    //级联表service

    @Autowired
    private YonghuService yonghuService;


    /**
    * 后端列表
    */
    @RequestMapping("/page")
    public R page(@RequestParam Map<String, Object> params, HttpServletRequest request){
        logger.debug("page方法:,,Controller:{},,params:{}",this.getClass().getName(),JSONObject.toJSONString(params));

        String role = String.valueOf(request.getSession().getAttribute("role"));
        if(StringUtil.isEmpty(role))
            return R.error(511,"权限为空");
        else if("用户".equals(role))
            params.put("yonghuId",request.getSession().getAttribute("userId"));
        params.put("newsDeleteStart",1);params.put("newsDeleteEnd",1);
        if(params.get("orderBy")==null || params.get("orderBy")==""){
            params.put("orderBy","id");
        }

        PageUtils page = newsService.queryPage(params);
        if (page == null) {
            return R.error(500, "查询分页数据异常，page 为 null");
        }
        //字典表数据转换
        List<NewsView> list =(List<NewsView>)page.getList();
        if (dictionaryService == null) {
            logger.error("dictionaryService 未正确注入，为 null");
            return R.error(500, "内部服务异常，dictionaryService 未就绪");
        }
        for(NewsView c:list){
            //修改对应字典表字段
            dictionaryService.dictionaryConvert(c, request);
        }
        return R.ok().put("data", page);
    }

    /**
    * 后端详情
    */
    @RequestMapping("/info/{id}")
    public R info(@PathVariable("id") Long id, HttpServletRequest request){
        logger.debug("info方法:,,Controller:{},,id:{}",this.getClass().getName(),id);
        NewsEntity news = newsService.selectById(id);
        if(news !=null){
            //entity转view
            NewsView view = new NewsView();
            BeanUtils.copyProperties( news , view );//把实体数据重构到view中

            //修改对应字典表字段
            dictionaryService.dictionaryConvert(view, request);
            return R.ok().put("data", view);
        }else {
            return R.error(511,"查不到数据");
        }

    }

    /**
    * 后端保存
    */
    @RequestMapping("/save")
    public R save(@RequestBody NewsEntity news, HttpServletRequest request){
        logger.debug("save方法:,,Controller:{},,news:{}",this.getClass().getName(),news.toString());

        String role = String.valueOf(request.getSession().getAttribute("role"));
        if(StringUtil.isEmpty(role))
            return R.error(511,"权限为空");

        Wrapper<NewsEntity> queryWrapper = new EntityWrapper<NewsEntity>()
            .eq("news_name", news.getNewsName())
            .eq("news_types", news.getNewsTypes())
            .eq("news_delete", news.getNewsDelete())
            ;

        logger.info("sql语句:"+queryWrapper.getSqlSegment());
        NewsEntity newsEntity = newsService.selectOne(queryWrapper);
        if(newsEntity==null){
            news.setInsertTime(new Date());
            news.setNewsDelete(1);
            news.setCreateTime(new Date());
            newsService.insert(news);
            return R.ok();
        }else {
            return R.error(511,"表中有相同数据");
        }
    }

    /**
    * 后端修改
    */
    @RequestMapping("/update")
    public R update(@RequestBody NewsEntity news, HttpServletRequest request){
        logger.debug("update方法:,,Controller:{},,news:{}",this.getClass().getName(),news.toString());

        String role = String.valueOf(request.getSession().getAttribute("role"));
//        if(StringUtil.isEmpty(role))
//            return R.error(511,"权限为空");
        //根据字段查询是否有相同数据
        Wrapper<NewsEntity> queryWrapper = new EntityWrapper<NewsEntity>()
            .notIn("id",news.getId())
            .andNew()
            .eq("news_name", news.getNewsName())
            .eq("news_types", news.getNewsTypes())
            .eq("news_delete", news.getNewsDelete())
            ;

        logger.info("sql语句:"+queryWrapper.getSqlSegment());
        NewsEntity newsEntity = newsService.selectOne(queryWrapper);
        if("".equals(news.getNewsPhoto()) || "null".equals(news.getNewsPhoto())){
                news.setNewsPhoto(null);
        }
        if(newsEntity==null){
            //  String role = String.valueOf(request.getSession().getAttribute("role"));
            //  if("".equals(role)){
            //      news.set
            //  }
            newsService.updateById(news);//根据id更新
            return R.ok();
        }else {
            return R.error(511,"表中有相同数据");
        }
    }

    /**
    * 删除
    */
    @RequestMapping("/delete")
    public R delete(@RequestBody Integer[] ids){
        logger.debug("delete:,,Controller:{},,ids:{}",this.getClass().getName(),ids.toString());
        ArrayList<NewsEntity> list = new ArrayList<>();
        for(Integer id:ids){
            NewsEntity newsEntity = new NewsEntity();
            newsEntity.setId(id);
            newsEntity.setNewsDelete(2);
            list.add(newsEntity);
        }
        if(list != null && list.size() >0){
            newsService.updateBatchById(list);
        }
        return R.ok();
    }


    /**
     * 批量上传
     */
    @RequestMapping("/batchInsert")
    public R save( String fileName){
        logger.debug("batchInsert方法:,,Controller:{},,fileName:{}",this.getClass().getName(),fileName);
        try {
            List<NewsEntity> newsList = new ArrayList<>();//上传的东西
            Map<String, List<String>> seachFields= new HashMap<>();//要查询的字段
            Date date = new Date();
            int lastIndexOf = fileName.lastIndexOf(".");
            if(lastIndexOf == -1){
                return R.error(511,"该文件没有后缀");
            }else{
                String suffix = fileName.substring(lastIndexOf);
                if(!".xls".equals(suffix)){
                    return R.error(511,"只支持后缀为xls的excel文件");
                }else{
                    URL resource = this.getClass().getClassLoader().getResource("static/upload/" + fileName);//获取文件路径
                    File file = new File(resource.getFile());
                    if(!file.exists()){
                        return R.error(511,"找不到上传文件，请联系管理员");
                    }else{
                        List<List<String>> dataList = PoiUtil.poiImport(file.getPath());//读取xls文件
                        dataList.remove(0);//删除第一行，因为第一行是提示
                        for(List<String> data:dataList){
                            //循环
                            NewsEntity newsEntity = new NewsEntity();
//                            newsEntity.setNewsName(data.get(0));                    //案例分析标题 要改的
//                            newsEntity.setNewsTypes(Integer.valueOf(data.get(0)));   //案例分析类型 要改的
//                            newsEntity.setNewsPhoto("");//照片
//                            newsEntity.setInsertTime(date);//时间
//                            newsEntity.setNewsContent("");//照片
//                            newsEntity.setNewsDelete(1);//逻辑删除字段
//                            newsEntity.setCreateTime(date);//时间
                            newsList.add(newsEntity);


                            //把要查询是否重复的字段放入map中
                        }

                        //查询是否重复
                        newsService.insertBatch(newsList);
                        return R.ok();
                    }
                }
            }
        }catch (Exception e){
            return R.error(511,"批量插入数据异常，请联系管理员");
        }
    }





    /**
    * 前端列表
    */
    @IgnoreAuth
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params, HttpServletRequest request){
        logger.debug("list方法:,,Controller:{},,params:{}",this.getClass().getName(),JSONObject.toJSONString(params));

        // 没有指定排序字段就默认id倒序
        if(StringUtil.isEmpty(String.valueOf(params.get("orderBy")))){
            params.put("orderBy","id");
        }
        PageUtils page = newsService.queryPage(params);

        //字典表数据转换
        List<NewsView> list =(List<NewsView>)page.getList();
        for(NewsView c:list)
            dictionaryService.dictionaryConvert(c, request); //修改对应字典表字段
        return R.ok().put("data", page);
    }

    /**
    * 前端详情
    */
    @RequestMapping("/detail/{id}")
    public R detail(@PathVariable("id") Long id, HttpServletRequest request){
        logger.debug("detail方法:,,Controller:{},,id:{}",this.getClass().getName(),id);
        NewsEntity news = newsService.selectById(id);
            if(news !=null){


                //entity转view
                NewsView view = new NewsView();
                BeanUtils.copyProperties( news , view );//把实体数据重构到view中

                //修改对应字典表字段
                dictionaryService.dictionaryConvert(view, request);
                return R.ok().put("data", view);
            }else {
                return R.error(511,"查不到数据");
            }
    }


    /**
    * 前端保存
    */
    @RequestMapping("/add")
    public R add(@RequestBody NewsEntity news, HttpServletRequest request){
        logger.debug("add方法:,,Controller:{},,news:{}",this.getClass().getName(),news.toString());
        Wrapper<NewsEntity> queryWrapper = new EntityWrapper<NewsEntity>()
            .eq("news_name", news.getNewsName())
            .eq("news_types", news.getNewsTypes())
            .eq("news_delete", news.getNewsDelete())
            ;
        logger.info("sql语句:"+queryWrapper.getSqlSegment());
        NewsEntity newsEntity = newsService.selectOne(queryWrapper);
        if(newsEntity==null){
            news.setInsertTime(new Date());
            news.setNewsDelete(1);
            news.setCreateTime(new Date());
        //  String role = String.valueOf(request.getSession().getAttribute("role"));
        //  if("".equals(role)){
        //      news.set
        //  }
        newsService.insert(news);
            return R.ok();
        }else {
            return R.error(511,"表中有相同数据");
        }
    }


}
