var app = new Vue({
    el: "#app",
    data: {
        //登录
        signInDialogVisible:false,
        signInUserName:null,
        signInPassword:null,
        signInRemember7Days:false,

        //注册
        signUpDialogVisible:false,
        signUpUserName:null,
        signUpPassword:null,
        signUpPasswordConfirm:null,

        //计划
        planTotalPage:10,
        planCurrentPage:1,
        planList:[
            {
                name:"A计划",
                date:"2020-02-02"
            },
            {
                name:"B计划",
                date:"2021-02-02"
            },
            {
                name:"C计划",
                date:"2021-02-02"
            }
        ],
        planName:null,
        planCpi:null,
        planAge:null,
        planInit:null,

        //现金流
        mpList:[
            {
                name:"A现金流",
                start:1,
                end:5,
                proportion:"80%",
                rate:"20%"
            },
            {
                name:"A现金流",
                start:1,
                end:5,
                proportion:"80%",
                rate:"20%"
            },
            {
                name:"A现金流",
                start:1,
                end:5,
                proportion:"80%",
                rate:"20%"
            },
        ],
        mpName:null,
        mpStart:null,
        mpEnd:null,
        mpProportion:null,
        mpRate:null,
        mpDialogVisible:false,

        //理财
        cfList:[
            {
                name:"A现金流",
                start:1,
                end:5,
                net:100000,
                rate:"引用CPI"
            },
            {
                name:"B现金流",
                start:3,
                end:7,
                net:-20000,
                rate:"引用CPI"
            },
            {
                name:"C现金流",
                start:1,
                end:20,
                net:50000,
                rate:"10%"
            }
        ],
        cfName:null,
        cfStart:null,
        cfEnd:null,
        cfNet:null,
        cfRate:null,
        cfDialogVisible:false,
        cfDialogUseCpi:false,

        //图表
        chartData: {
            columns: ['日期', '访问用户', '下单用户', '下单率'],
            rows: [
              { '日期': '1/1', '访问用户': 1393, '下单用户': 1093, '下单率': 0.32 },
              { '日期': '1/2', '访问用户': 3530, '下单用户': 3230, '下单率': 0.26 },
              { '日期': '1/3', '访问用户': 2923, '下单用户': 2623, '下单率': 0.76 },
              { '日期': '1/4', '访问用户': 1723, '下单用户': 1423, '下单率': 0.49 },
              { '日期': '1/5', '访问用户': 3792, '下单用户': 3492, '下单率': 0.323 },
              { '日期': '1/6', '访问用户': 4593, '下单用户': 4293, '下单率': 0.78 }
            ]
        },

        //计算结果
        calcResultList:[
            {
                year:1,
                age:25,
                init:10000,
                earnRate:"8%",
                earn:800,
                net:10000,
                balance:20800
            },
            {
                year:1,
                age:25,
                init:10000,
                earnRate:"8%",
                earn:800,
                net:10000,
                balance:20800
            },
            {
                year:1,
                age:25,
                init:10000,
                earnRate:"8%",
                earn:800,
                net:10000,
                balance:20800
            }
        ]

    },
    methods:{
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
        onPagePlan: function(pageNo){

        },
        onClickPlanDelete: function(index){
            this.planList.splice(index,1);
        },
        onClickPlanSave: function(){

        },
        onClickPlanAdd: function(){

        },
        onClickPlanRow: function(row){
            alert(row);
        },


        //计算
        onClickPlanCalc: function(){

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
            onCloseSignInDialog();
        },
        onClickSignInFromSignUp: function(){
            this.signUpDialogVisible = false;
            this.signInDialogVisible = true;
            onCloseSignUpDialog();
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

        },
        postUserSignUp: function(){

        },


        //现金流
        onClickCfAdd: function(){
            this.cfDialogVisible = true;
        },
        onClickCfRow: function(row){

        },
        onClickCfSave: function(){

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
        },


        //理财
        onClickMpAdd: function(){
            this.mpDialogVisible = true;
        },
        onClickMpRow: function(row){

        },
        onClickMpSave: function(){

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