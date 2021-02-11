var app = new Vue({
    el: "#app",
    data: {
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
            },
        ],

        planDetailsList:null,

        planName:null,

        planCpi:null,

        planAge:null,

        planInit:null,

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

        calcResultList:null

    },
    methods:{
        onClickReward: function(){
            alert("打赏");
        },

        onClickPlanDelete: function(index){
            this.planList.splice(index,1);
        },

        onClickPlanSave: function(){

        },

        onClickPlanCalc: function(){

        },

        onClickPlanAdd: function(){

        },

        onClickPlanRow: function(row){
            alert(row);
        },

        onClickSignIn: function(){

        },

        onClickSignUp: function(){

        },

        onClickPlanDetailsAdd: function(){

        },

        onClickPlanDetailsRow: function(row, column, event){

        },

        onClickPlanDetailsDelete: function(index){
            this.planList.splice(index,1);
        },

        onPagePlan: function(pageNo){

        }

    }
});