const base = {
    get() {
        return {
            url : "http://localhost:8080/fanzhapingtai/",
            name: "fanzhapingtai",
            // 退出到首页链接
            indexUrl: 'http://localhost:8080/fanzhapingtai/front/index.html'
        };
    },
    getProjectName(){
        return {
            projectName: "防诈小卫士后台"
        } 
    }
}
export default base
