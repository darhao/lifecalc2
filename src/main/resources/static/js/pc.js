axios.defaults.baseURL = "http://localhost";
//设置content-type为application/x-www-form-urlencoded并且利用params传参才能跨域
axios.defaults.headers.post["Content-Type"] = "application/x-www-form-urlencoded";

var app = new Vue({
    el: "#app",

    mounted: function () {
        //vue加载完毕后隐藏loading
        document.getElementById('app').style.display = 'block';
        document.getElementById('appLoading').style.display = 'none';
        //调用计算
        this.onClickPlanCalc();
        //取出userInfo
        if(localStorage.getItem("userInfo") != null){
            this.userInfo = JSON.parse(localStorage.getItem("userInfo"));
        }else if(sessionStorage.getItem("userInfo") != null){
            this.userInfo = JSON.parse(sessionStorage.getItem("userInfo"));
        }else{
            return;
        }
        //获取计划列表数据
        this.postPagePlan(1);
    },

    data: {
        //加载
        firstLoadFlag: true,
        loading: false,

        //登录
        signInDialogVisible:false,
        signInUserName:null,
        signInPassword:null,
        signInRemember7Days:false,
        userInfo:null,

        //注册
        signUpDialogVisible:false,
        signUpUserName:null,
        signUpPassword:null,
        signUpPasswordConfirm:null,

        //计划
        planTotalPage:null,
        planCurrentPage:null,
        planList:[],
        planName:"我的计划A",
        planCpi:"3",
        planAge:"25",
        planInit:"100000",
        planUpdateId:null,

        //理财
        mpList:[
            {
                name: "股票型基金",
                start: "1",
                end: "11",
                earnRate: "20",
                earnProportion: "100"
            },
            {
                name: "债券型基金",
                start: "11",
                end: "60",
                earnRate: "8",
                earnProportion: "100"
            }
        ],
        mpName:null,
        mpStart:null,
        mpEnd:null,
        mpProportion:null,
        mpRate:null,
        mpDialogVisible:false,
        mpUpdateRow:null,

        //现金流
        cfList:[
            {
                name: "打工",
                start: "1",
                end: "11",
                net: "150000",
                netRate: "引用CPI"
            },
            {
                name: "退休",
                start: "11",
                end: "60",
                net: "-85000",
                netRate: "引用CPI"
            },
            {
                name: "买房首付",
                start: "4",
                end: "5",
                net: "-1000000",
                netRate: "0"
            },
            {
                name: "买房月供",
                start: "5",
                end: "35",
                net: "-96000",
                netRate: "0"
            },
            {
                name: "买房后房租减免",
                start: "4",
                end: "60",
                net: "48000",
                netRate: "引用CPI"
            },
            {
                name: "社保",
                start: "35",
                end: "60",
                net: "36000",
                netRate: "引用CPI"
            }
        ],
        cfName:null,
        cfStart:null,
        cfEnd:null,
        cfNet:null,
        cfRate:null,
        cfDialogVisible:false,
        cfDialogUseCpi:false,
        cfUpdateRow:null,

        //图表
        chartData: {
            columns: ["年龄", "年初总资产"],
            rows: []
        },
        chartSettings: {
            metricsType: {
                "年初总资产": "0.0a"
            },
            area: true
        },

        //计算结果
        calcResultList: []

    },
    methods:{
        showSystemErrorMessage: function(err){
            this.$message({
                message: "系统错误，请联系作者Email: darhao@qq.com",
                type: "error"
            });
            console.log(err);
        },

        showSignInStateExpirMessage: function(){
            this.$message({
                message: "登录状态已过期，请重新登录",
                type: "warning"
            });
        },

        //打赏
        onClickReward: function(){
            this.$alert(
            '<div style="text-align:center;"><img src="../image/money.jpg" style="height:320px;"/></div>',
            '喜欢的话可以打赏一下人家吗(/≧▽≦)/',
            {
                dangerouslyUseHTMLString: true,
                showConfirmButton: false
            });
        },


        //计划
        postPagePlan: function(pageNo){
            //开始loading
            this.loading = true;
            //请求
            let params = new URLSearchParams();
            params.append("token", this.userInfo.token);
            params.append("pageNo", pageNo);
            let that = this;
            axios.post("/plan/list", params)
                .then(function (response) {
                    if(response.data.code === 200){
                        //设置分页条
                        that.planCurrentPage = response.data.data.currentPage;
                        that.planTotalPage = response.data.data.totalPage;
                        that.planList = response.data.data.records;
                        //如果有至少一条计划就加载到右边（只在页面首次加载触发）
                        if(that.planList.length > 0 && that.firstLoadFlag){
                            that.onClickPlanRow(0);
                        }
                        //设置首次加载标记
                        that.firstLoadFlag = false;
                    }else if(response.data.code === 211){
                        //清除userInfo
                        that.onClickSignOut();
                        that.showSignInStateExpirMessage();
                    }else{
                        that.showSystemErrorMessage(response.data.data);
                    }
                 }).catch(function (error) {
                    that.showSystemErrorMessage(error);
                 }).finally(function(){
                    that.loading = false;
                 });
        },

        onClickPlanCopy: function(index){
            let that = this;
            //开始loading
            that.loading = true;
            //请求
            let params = new URLSearchParams();
            params.append("token", that.userInfo.token);
            params.append("planId", that.planList[index].id);
            axios.post("/plan/copy", params)
                .then(function (response) {
                    if(response.data.code === 200){
                        //刷新
                        that.postPagePlan(that.planCurrentPage);
                        that.$message({
                            type: 'success',
                            message: '复制成功！'
                        });
                    }else if(response.data.code === 211){
                        //清除userInfo
                        that.onClickSignOut();
                        that.loading = false;
                        that.showSignInStateExpirMessage();
                    }else{
                        that.showSystemErrorMessage(response.data.data);
                        that.loading = false;
                    }
                 }).catch(function (error) {
                    that.showSystemErrorMessage(error);
                    that.loading = false;
                 });
        },

        onClickPlanDelete: function(index){
            let that = this;
            //确认删除框
            this.$confirm('确定要删除该计划吗？', '警告', {
              confirmButtonText: '确定',
              cancelButtonText: '取消',
              type: 'warning'
            }).then(() => {
                //开始loading
                that.loading = true;
                //请求
                let params = new URLSearchParams();
                params.append("token", that.userInfo.token);
                params.append("planId", that.planList[index].id);

                axios.post("/plan/remove", params)
                    .then(function (response) {
                        if(response.data.code === 200){
                            //刷新
                            that.postPagePlan(that.planCurrentPage);
                            //如删除的id为右侧正在编辑的id则执行清空操作
                            if(that.planList[index].id === that.planUpdateId){
                                that.clearPlan();
                            }
                            that.$message({
                                type: 'success',
                                message: '删除成功！'
                            });
                        }else if(response.data.code === 211){
                            //清除userInfo
                            that.onClickSignOut();
                            that.loading = false;
                            that.showSignInStateExpirMessage();
                        }else{
                            that.showSystemErrorMessage(response.data.data);
                            that.loading = false;
                        }
                     }).catch(function (error) {
                        that.showSystemErrorMessage(error);
                        that.loading = false;
                     });
            });
        },
        onClickPlanSave: function(){
            //如果没有登录就弹出登录框
            if(this.userInfo === null){
                this.onClickSignIn();
                return;
            }
            //参数校验
            if(this.planName === null || this.planName.trim().length === 0){
                this.$message({message: "请填写名称", type: "error"});
                return;
            }
            //顶部表单校验
            if(!this.topFormCheck()){
                return;
            }
            //开始loading
            this.loading = true;
            //封装
            let finalList = this.mpList.concat(this.cfList);
            let body = {
                age: Number(this.planAge),
                cpi: Number(this.planCpi),
                init: Number(this.planInit),
                items: finalList
            };
            //判断保存还是更新
            if(this.planUpdateId === null){
                //请求增加
                let params = new URLSearchParams();
                params.append("token", this.userInfo.token);
                params.append("name", this.planName);
                params.append("planJson", JSON.stringify(body));
                let that = this;
                axios.post("/plan/add", params)
                    .then(function (response) {
                        if(response.data.code === 200){
                            //刷新
                            that.postPagePlan(that.planCurrentPage);
                            that.$message({
                                message: "新计划保存成功",
                                type: "success"
                            });
                            //将新id设置为当前更新行id
                            that.planUpdateId = response.data.data;
                        }else if(response.data.code === 211){
                            //清除userInfo
                            that.onClickSignOut();
                            that.loading = false;
                            that.showSignInStateExpirMessage();
                        }else{
                            that.showSystemErrorMessage(response.data.data);
                            that.loading = false;
                        }
                     }).catch(function (error) {
                        that.showSystemErrorMessage(error);
                        that.loading = false;
                     });
            }else{
                //请求修改
                let params = new URLSearchParams();
                params.append("token", this.userInfo.token);
                params.append("name", this.planName);
                params.append("planId", this.planUpdateId);
                params.append("planJson", JSON.stringify(body));
                let that = this;
                axios.post("/plan/update", params)
                    .then(function (response) {
                        if(response.data.code === 200){
                            //刷新
                            that.postPagePlan(that.planCurrentPage);
                            that.$message({
                                message: "计划修改成功",
                                type: "success"
                            });
                        }else if(response.data.code === 211){
                            //清除userInfo
                            that.onClickSignOut();
                            that.loading = false;
                            that.showSignInStateExpirMessage();
                        }else{
                            that.showSystemErrorMessage(response.data.data);
                            that.loading = false;
                        }
                     }).catch(function (error) {
                        that.showSystemErrorMessage(error);
                        that.loading = false;
                     });
            }
        },

        topFormCheck: function(){
            if(this.planAge === null || this.planAge.trim().length === 0 || !Number.isInteger(Number(this.planAge)) || Number(this.planAge) < 0){
                this.$message({message: "年龄请填写不小于0的整数", type: "error"});
                return false;
            }
            if(this.planCpi === null || this.planCpi.trim().length === 0 || !Number.isInteger(Number(this.planCpi))){
                this.$message({message: "CPI请填写整数", type: "error"});
                return false;
            }
            if(this.planInit === null || this.planInit.trim().length === 0 || !Number.isInteger(Number(this.planInit))){
                this.$message({message: "资产请填写整数", type: "error"});
                return false;
            }
            return true;
        },

        clearPlan: function(){
            this.planUpdateId = null;
            this.planName = null;
            this.planCpi = null;
            this.planAge = null;
            this.planInit = null;
            this.mpList = [];
            this.cfList = [];
            this.chartData.rows = [];
            this.calcResultList = [];
        },

        onClickPlanAdd: function(){
            //清空右侧数据
            this.clearPlan();
            this.$message({
                message: "请在右侧填写计算参数，完成后保存即可",
                type: "success"
            });
        },
        onClickPlanRow: function(row){
            this.planUpdateId = this.planList[row].id;
            //开始loading
            this.loading = true;
            //请求
            let params = new URLSearchParams();
            params.append("token", this.userInfo.token);
            params.append("planId", this.planList[row].id);
            let that = this;
            axios.post("/plan/get", params)
                .then(function (response) {
                    if(response.data.code === 200){
                        //解析
                        let data = response.data.data;
                        that.planName = data.name;
                        that.planCpi = data.body.cpi.toString();
                        that.planAge = data.body.age.toString();
                        that.planInit = data.body.init.toString();
                        that.mpList = [];
                        that.cfList = [];
                        for(let i = 0; i < data.body.items.length; i++){
                            let obj = {};
                            obj.name = data.body.items[i].name;
                            obj.start = data.body.items[i].start.toString();
                            obj.end = data.body.items[i].end.toString();
                            if(data.body.items[i].net != null){
                                obj.net = data.body.items[i].net.toString();
                                obj.netRate = data.body.items[i].netRate.toString();
                                that.cfList.push(obj);
                            }else{
                                obj.earnRate = data.body.items[i].earnRate.toString();
                                obj.earnProportion = data.body.items[i].earnProportion.toString();
                                that.mpList.push(obj);
                            }
                        }
                         //调用计算
                        that.onClickPlanCalc();
                    }else if(response.data.code === 211){
                        //清除userInfo
                        that.onClickSignOut();
                        that.showSignInStateExpirMessage();
                    }else{
                        that.showSystemErrorMessage(response.data.data);
                    }
                 }).catch(function (error) {
                    that.showSystemErrorMessage(error);
                 }).finally(function(){
                    that.loading = false;
                 });
        },


        //计算
        onClickPlanCalc: function(){
            //顶部表单校验
            if(!this.topFormCheck()){
                return;
            }
            //清空图表和计算结果
            this.chartData.rows = [];
            this.calcResultList = [];
            //计算x轴长度（取理财和现金流中最大的结束年）
            let x = 0;
            for(i in this.mpList){
                let mp = this.mpList[i];
                if(Number(mp.end) > x){
                    x = Number(mp.end);
                }
            }
            for(i in this.cfList){
                let cf = this.cfList[i];
                if(Number(cf.end) > x){
                    x = Number(cf.end);
                }
            }
            if(x === 0){
                this.$message({
                    message: "需填写现金流或理财表至少1项，以完成计算",
                    type: "info"
                });
                return;
            }
            //定义计算过程数组并初始化元素，长度为x
            let cps = new Array(x);
            for(let i = 0; i < x; i++){
                cps[i] = {
                    year: 1 + i,
                    age: Number(this.planAge) + i,
                    init: 0,
                    earnRate: 0,
                    earn: 0,
                    net: 0,
                    balance: 0
                };
            }
            //遍历现金流表
            for(a in this.cfList){
                let cf = this.cfList[a];
                let i = Number(cf.start);
                let j = 0;
                let k = cf.netRate === "引用CPI" ? this.planCpi : cf.netRate;
                while(i < Number(cf.end)){
                    cps[i-1].net += cf.net * Math.pow(1 + k/100.0, j);
                    i++;
                    j++;
                }
            }
            //遍历理财表
            for(a in this.mpList){
                let mp = this.mpList[a];
                let i = Number(mp.start);
                while(i < Number(mp.end)){
                    cps[i-1].earnRate += (mp.earnRate/100.0) * (mp.earnProportion/100.0);
                    i++;
                }
            }
            //遍历cps并完成其它计算项
            for(let i = 0; i < x; i++){
                //计算年初结余
                if(i === 0){
                    cps[i].init = Number(this.planInit);
                }else{
                    cps[i].init = cps[i-1].balance;
                }
                //计算总理财收入
                cps[i].earn = cps[i].init * cps[i].earnRate;
                //计算年末结余
                cps[i].balance = cps[i].init + cps[i].earn + cps[i].net;
            }
            //设置计算过程数组，百分号转化，保留个位
            for(let i = 0; i < x; i++){
                cps[i].init = cps[i].init.toFixed(0);
                cps[i].balance = cps[i].balance.toFixed(0);
                cps[i].net = cps[i].net.toFixed(0);
                cps[i].earn = cps[i].earn.toFixed(0);
                cps[i].earnRate = (cps[i].earnRate * 100).toFixed(0) + "%";
            }
            this.calcResultList = cps;
            //设置图表
            let chartRows = new Array(x);
            for(let i = 0; i < x; i++){
                chartRows[i] = {
                    "年龄": this.calcResultList[i].age.toString(),
                    "年初总资产": this.calcResultList[i].init
                };
            }
            this.chartData.rows = chartRows;
        },


        //登录注册
        onClickSignIn: function(){
            this.signInDialogVisible = true;
        },
        onClickSignUp: function(){
            this.signUpDialogVisible = true;
        },
        onClickSignUpFromSignIn: function(){
            this.signUpDialogVisible = true;
            this.signInDialogVisible = false;
            this.onCloseSignInDialog();
        },
        onClickSignInFromSignUp: function(){
            this.signUpDialogVisible = false;
            this.signInDialogVisible = true;
            this.onCloseSignUpDialog();
        },
        onCloseSignInDialog: function(){
            this.signInUserName = null;
            this.signInPassword = null;
            this.signInRemember7Days = false;
        },
        onCloseSignUpDialog: function(){
            this.signUpUserName = null;
            this.signUpPassword = null;
            this.signUpPasswordConfirm = null;
        },
        postUserSignIn: function(){
            //开始loading
            this.loading = true;
            //参数校验
            if(this.signInUserName === null ||
               this.signInUserName.trim().length === 0 ||
               this.signInPassword === null ||
               this.signInPassword.trim().length === 0){
                this.$message({
                    message: "用户名密码不能为空",
                    type: "error"
                });
                this.loading = false;
                return;
            }
            if(this.signInPassword.trim().length > 16 || this.signInPassword.trim().length < 6){
                this.$message({
                    message: "密码长度必须为6-16",
                    type: "error"
                });
                this.loading = false;
                return;
            }
            //请求
            let params = new URLSearchParams();
            params.append("name", this.signInUserName);
            params.append("password", this.signInPassword);
            let that = this;
            axios.post("/user/signIn", params)
                .then(function (response) {
                    if(response.data.code === 200){
                        //设置userInfo
                        that.userInfo = response.data.data;
                        //根据选项存储userInfo
                        if(that.signInRemember7Days){
                            localStorage.setItem("userInfo", JSON.stringify(that.userInfo));
                        }else{
                            sessionStorage.setItem("userInfo", JSON.stringify(that.userInfo));
                        }
                        that.$message({
                            message: "登录成功",
                            type: "success"
                        });
                        //刷新列表
                        that.firstLoadFlag = true;
                        that.postPagePlan(1);
                        //关闭对话框
                        that.signInDialogVisible = false;
                        that.onCloseSignInDialog();
                    }else{
                        that.$message({
                            message: response.data.data,
                            type: "error"
                        });
                    }
                 }).catch(function (error) {
                    that.$message({
                        message: error,
                        type: "error"
                    });
                 }).finally(function(){
                    that.loading = false;
                 });
        },
        postUserSignUp: function(){
            //开始loading
            this.loading = true;
            //参数校验
            if(this.signUpUserName === null ||
               this.signUpPassword === null ||
               this.signUpPasswordConfirm === null ||
               this.signUpUserName.trim().length === 0 ||
               this.signUpPassword.trim().length === 0 ||
               this.signUpPasswordConfirm.trim().length === 0){
                this.$message({
                    message: "用户名密码不能为空",
                    type: "error"
                });
                this.loading = false;
                return;
            }
            if(this.signUpPassword.trim().length > 16 || this.signUpPassword.trim().length < 6){
                this.$message({
                    message: "密码长度必须为6-16",
                    type: "error"
                });
                this.loading = false;
                return;
            }
            if(this.signUpPassword.trim() != this.signUpPasswordConfirm.trim()){
                this.$message({
                    message: "密码不一致",
                    type: "error"
                });
                this.loading = false;
                return;
            }
            //请求
            let params = new URLSearchParams();
            params.append("name", this.signUpUserName);
            params.append("password", this.signUpPassword);
            let that = this;
            axios.post("/user/signUp", params)
                .then(function (response) {
                    if(response.data.code === 200){
                        //设置userInfo
                        that.userInfo = response.data.data;
                        //根据选项存储userInfo
                        if(that.signInRemember7Days){
                            localStorage.setItem("userInfo", JSON.stringify(that.userInfo));
                        }else{
                            sessionStorage.setItem("userInfo", JSON.stringify(that.userInfo));
                        }
                        that.$message({
                            message: "注册并登录成功",
                            type: "success"
                        });
                        //关闭对话框
                        that.signUpDialogVisible = false;
                        that.onCloseSignUpDialog();
                    }else{
                        that.$message({
                            message: response.data.data,
                            type: "error"
                        });
                    }
                 }).catch(function (error) {
                    that.$message({
                        message: error,
                        type: "error"
                    });
                 }).finally(function(){
                    that.loading = false;
                 });
        },
        onClickSignOut: function(){
            localStorage.removeItem("userInfo");
            sessionStorage.removeItem("userInfo");
            this.userInfo = null;
        },


        //现金流
        onClickCfAdd: function(){
            this.cfDialogVisible = true;
            this.cfUpdateRow = null;
        },
        onClickCfRow: function(row){
            this.cfName = this.cfList[row].name;
            this.cfStart = this.cfList[row].start.toString();
            this.cfEnd = this.cfList[row].end.toString();
            this.cfNet = this.cfList[row].net.toString();
            if(this.cfList[row].netRate === "引用CPI"){
                this.cfDialogUseCpi = true;
            }else{
                this.cfRate = this.cfList[row].netRate.toString();
            }
            this.cfDialogVisible = true;
            this.cfUpdateRow = row;
        },
        onClickCfSave: function(){
            //参数校验
            if(this.cfName === null || this.cfName.trim().length === 0){
                this.$message({message: "请填写名称", type: "error"});
                return;
            }
            if(this.cfStart === null || this.cfStart.trim().length === 0 || !Number.isInteger(Number(this.cfStart)) || Number(this.cfStart) < 1){
                this.$message({message: "起始年请填写大于1的整数", type: "error"});
                return;
            }
            if(this.cfEnd === null || this.cfEnd.trim().length === 0 || !Number.isInteger(Number(this.cfEnd)) || Number(this.cfEnd) < 1){
                this.$message({message: "结束年请填写大于1的整数", type: "error"});
                return;
            }
            if(Number(this.cfEnd) <= Number(this.cfStart)){
                this.$message({message: "起始年必须小于结束年", type: "error"});
                return;
            }
            if(this.cfNet === null || this.cfNet.trim().length === 0 || !Number.isInteger(Number(this.cfNet))){
                this.$message({message: "年净收入请填写整数", type: "error"});
                return;
            }
            if(this.cfRate === null || this.cfRate.trim().length === 0 || !Number.isInteger(Number(this.cfRate))){
                if(!this.cfDialogUseCpi){
                    this.$message({message: "年入增长请填写整数", type: "error"});
                    return;
                }
            }
            let obj = {
                name: this.cfName,
                start: Number(this.cfStart),
                end: Number(this.cfEnd),
                net: Number(this.cfNet),
                netRate: this.cfDialogUseCpi ? "引用CPI" : Number(this.cfRate)
            }
            //判断是新增还是修改
            if(this.cfUpdateRow != null){
                this.cfList.splice(this.cfUpdateRow,1,obj);
            }else{
                this.cfList.push(obj);
            }
            this.onCloseCfDialog();
            this.cfDialogVisible = false;
        },
        onClickCfDelete: function(index){
            this.cfList.splice(index,1);
        },
        onCloseCfDialog: function(){
            this.cfName = null;
            this.cfStart = null;
            this.cfEnd = null;
            this.cfNet = null;
            this.cfRate = null;
            this.cfDialogUseCpi = false;
        },


        //理财
        onClickMpAdd: function(){
            this.mpDialogVisible = true;
            this.mpUpdateRow = null;
        },
        onClickMpRow: function(row){
            this.mpName = this.mpList[row].name;
            this.mpStart = this.mpList[row].start.toString();
            this.mpEnd = this.mpList[row].end.toString();
            this.mpRate = this.mpList[row].earnRate.toString();
            this.mpProportion = this.mpList[row].earnProportion.toString();
            this.mpDialogVisible = true;
            this.mpUpdateRow = row;
        },
        onClickMpSave: function(){
            //参数校验
            if(this.mpName === null || this.mpName.trim().length === 0){
                this.$message({message: "请填写名称", type: "error"});
                return;
            }
            if(this.mpStart === null || !Number.isInteger(Number(this.mpStart)) || Number(this.mpStart) < 1){
                this.$message({message: "起始年请填写大于1的整数", type: "error"});
                return;
            }
            if(this.mpEnd === null || !Number.isInteger(Number(this.mpEnd)) || Number(this.mpEnd) < 1){
                this.$message({message: "结束年请填写大于1的整数", type: "error"});
                return;
            }
            if(Number(this.mpEnd) <= Number(this.mpStart)){
                this.$message({message: "起始年必须小于结束年", type: "error"});
                return;
            }
            if(this.mpProportion === null || !Number.isInteger(Number(this.mpProportion))
                || Number(this.mpProportion) < 1 || Number(this.mpProportion) > 100){
                this.$message({message: "仓位占比请填写1-100的整数", type: "error"});
                return;
            }
            if(this.mpRate === null || !Number.isInteger(Number(this.mpRate))){
                this.$message({message: "理财年化请填写整数", type: "error"});
                return;
            }
            let obj = {
                name: this.mpName,
                start: Number(this.mpStart),
                end: Number(this.mpEnd),
                earnRate: Number(this.mpRate),
                earnProportion: Number(this.mpProportion)
            }
            //判断是新增还是修改
            if(this.mpUpdateRow != null){
                this.mpList.splice(this.mpUpdateRow,1,obj);
            }else{
                this.mpList.push(obj);
            }
            this.onCloseMpDialog();
            this.mpDialogVisible = false;
        },
        onClickMpDelete: function(index){
            this.mpList.splice(index,1);
        },
        onCloseMpDialog: function(){
            this.mpName = null;
            this.mpStart = null;
            this.mpEnd = null;
            this.mpProportion = null;
            this.mpRate = null;
        },

    }
});