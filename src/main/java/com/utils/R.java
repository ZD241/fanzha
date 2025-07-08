package com.utils;

import org.springframework.http.HttpStatus;

import java.util.HashMap;
import java.util.Map;

/**
 * 返回数据
 */
public class R extends HashMap<String, Object> {
	private static final long serialVersionUID = 1L;
	
	public R() {
		put("code", 0);
	}
	
	public static R error() {
		return error(500, "未知异常，请联系管理员");
	}
	
	public static R error(String msg) {
		return error(500, msg);
	}
	
	public static R error(int code, String msg) {
		R r = new R();
		r.put("code", code);
		r.put("msg", msg);
		return r;
	}

	public static R ok(String msg) {
		R r = new R();
		r.put("code", 200);
		r.put("msg", msg);
		return r;
	}

	
	public static R ok(Map<String, Object> map) {
		R r = new R();
		r.put("code", 200);
		r.putAll(map);
		return r;
	}

	public static R ok() {

			R r = new R();
			r.put("code", 200);
			return r;

	}

	public R put(String key, Object value) {
		super.put(key, value);
		return this;
	}

   private int code;
	public int getCode() {


		// 其他属性、构造方法等


		return (Integer) this.get("code");
	}
	public void setCode(int code) {
		this.code = code;
	}
	private String msg;
	public String getMsg() {

		return (String) this.get("msg");
	}
	public void setMsg(String msg) {
		this.msg = msg;
	}

}
