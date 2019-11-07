
function dsSelect (listId){
    let obj = {};
    if(!dsSelect_tools.notNull(listId)){
        alert("初始化失败，请检查table表的id参数！");
        return;
    }

    //初始化
    obj = (function(obj){
        obj.listId = listId;
        obj.multiSelect = false;
        return obj;
    })(obj);
    obj.init=function(){
        dsSelect_take.initView(obj);
        dsSelect_take.initEvent(obj);
    };
    /**
     * 设置左侧数据
     * @param datasArray
     */
    obj.setLeftData=function(datasArray,showParam){
        dsSelect_take.setLeftDatas(this,datasArray,showParam);
        dsSelect_take.initEvent(obj);
    };
    /**
     * 设置右侧数据
     * @param datasArray
     */
    obj.setRightData=function(datasArray,showParam){
        dsSelect_take.setRightDatas(this,datasArray,showParam);
        dsSelect_take.initEvent(obj);
    };
    /**
     * 获取左侧数据
     */
    obj.getSelectLeftValus = function(){
        return dsSelect_tools.eventGetValueLR(obj,"l");
    };
    /**
     * 获取右侧的数据
     */
    obj.getSelectRightValus = function(){
        return dsSelect_tools.eventGetValueLR(obj,"r");
    };
    obj.disableButtons = function(){
        dsSelect_take.disableEvent(obj);
    };
    obj.restartButtons = function () {
        dsSelect_take.restartButtonsEvent(this);
        dsSelect_take.initEvent(obj);
    };

    return obj;
};

let dsSelect_take={
    /**
     * 初始化显示
     * @param obj
     */
    "initView":function(obj){
        let listId = obj.listId;
        $("#"+listId).html(dsSelect_models.baseModel());
    },
    /**
     * 初始化事件
     * @param obj
     */
    "initEvent":function(obj){
        let listId = obj.listId;
        $("#"+listId+" .dsList li").off();

        dsSelect_tools.eventTakeWithLR(obj,"l");
        dsSelect_tools.eventTakeWithLR(obj,"r");
        dsSelect_tools.eventSelectToRight(obj);
        dsSelect_tools.eventSelectToLeft(obj);
        dsSelect_tools.eventAllToRight(obj);
        dsSelect_tools.eventAllToLeft(obj);
    },
    /**
     * 设置左侧数据
     */
    "setLeftDatas":function(obj,datasArray,showParam){
        dsSelect_tools.setDatasWithLR(obj,"l",datasArray,showParam);
    },
    /**
     * 设置右侧数据
     */
    "setRightDatas":function(obj,datasArray,showParam){
        dsSelect_tools.setDatasWithLR(obj,"r",datasArray,showParam);
    },
    /**
     * 禁止按钮事件
     */
    "disableEvent":function(obj){
        let listId = obj.listId;
        let selectToRightBt  = $("#"+listId+" .selectRight");
        let selectToLeftBt  = $("#"+listId+" .selectLeft");
        let allToRightBt  = $("#"+listId+" .allRight");
        let allToLeftBt  = $("#"+listId+" .allLeft");
        allToLeftBt.off();
        allToRightBt.off();
        selectToLeftBt.off();
        selectToRightBt.off();
        $(".dsButton").css("background-color","#AAAAAA");
    },
    /**
     * 重启按钮事件
     */
    "restartButtonsEvent":function(obj){
        $(".dsButton").css("background-color","#485d74");
    }
};

let dsSelect_models={
    /**
     * 初始模板
     * @returns {string}
     */
    "baseModel":function(){
        let str = '<div class="left">' +
            '<ul class="dsList">' +
            '</ul>' +
            '</div>' +
            '<div class="center">' +
            '<div class="dsButton selectRight"> > </div>' +
            '<div class="dsButton selectLeft"> < </div>' +
            '<div class="dsButton allRight"> >>> </div>' +
            '<div class="dsButton allLeft"> <<< </div>' +
            '</div>' +
            '<div class="right">'+
            '<ul class="dsList">' +
            '</ul>' +
            '</div>';
        return str;
    },
    /**
     * 选择模板
     */
    "selectItemModel":function(uid,value){
        let str='<li u="'+uid+'">'+value+'</li>';
        return str;
    }
};

let dsSelect_tools = {
    /**
     * 不为空或者null
     * @param str 要判断的数据字段
     */
    "notNull":function(str){
        return str!=null && str!="";
    },
    /**
     * 设置点击事件
     * @param lr
     */
    "eventTakeWithLR":function(obj,lr){
        let lrtemp = ".left";
        if(lr=="r"){
            lrtemp =".right";
        }
        let listId = obj.listId;
        $("#"+listId+" "+lrtemp+" .dsList li").on("mousedown",function(event){
            if (event.ctrlKey && event.button == 0) {
                if($(this).hasClass("selectItem")){
                    $(this).removeClass("selectItem")
                }else{
                    $(this).addClass("selectItem");
                }

            }else{
                if($(this).hasClass("selectItem")){
                    $(this).removeClass("selectItem")
                }else{
                    if(!obj.multiSelect){
                        $("#"+listId+" "+lrtemp+" .dsList li").not(this).removeAttr("class")
                    }
                    $(this).addClass("selectItem");
                }
            }

        })
    },
    /**
     * 已选择移动到右
     * @param obj
     */
    "eventSelectToRight":function(obj){
        let listId = obj.listId;
        let selectToRightBt  = $("#"+listId+" .selectRight");
        let rightListObj = $("#"+listId+" .right .dsList");
        selectToRightBt.off();
        selectToRightBt.on("mousedown",function(){
            let selectObjs = $("#"+listId+" .left .dsList .selectItem");
            if(selectObjs.length>0){
                selectObjs.removeAttr("class");
                selectObjs.appendTo(rightListObj);
                dsSelect_take.initEvent(obj);
            }

        });

    },
    /**
     * 已选择移动到左
     * @param obj
     */
    "eventSelectToLeft":function(obj){
        let listId = obj.listId;
        let selectToLeftBt  = $("#"+listId+" .selectLeft");
        let leftListObj = $("#"+listId+" .left .dsList");
        selectToLeftBt.off();
        selectToLeftBt.on("mousedown",function(){
            let selectObjs = $("#"+listId+" .right .dsList .selectItem");
            if(selectObjs.length>0) {
                selectObjs.removeAttr("class");
                selectObjs.appendTo(leftListObj);
                dsSelect_take.initEvent(obj);
            }
        });
    },
    /**
     * 所有的去右边
     * @param obj
     */
    "eventAllToRight":function(obj){
        let listId = obj.listId;
        let allToRightBt  = $("#"+listId+" .allRight");
        let rightListObj = $("#"+listId+" .right .dsList");
        allToRightBt.off();
        allToRightBt.on("mousedown",function(){
            let selectObjs = $("#"+listId+" .left .dsList li");
            if(selectObjs.length>0){
                selectObjs.removeAttr("class");
                selectObjs.appendTo(rightListObj);
                dsSelect_take.initEvent(obj);
            }

        });
    },
    /**
     * 所有的去左边
     * @param obj
     */
    "eventAllToLeft":function(obj){
        let listId = obj.listId;
        let allToLeftBt  = $("#"+listId+" .allLeft");
        let leftListObj = $("#"+listId+" .left .dsList");
        allToLeftBt.off();
        allToLeftBt.on("mousedown",function(){
            let selectObjs = $("#"+listId+" .right .dsList li");
            if(selectObjs.length>0) {
                selectObjs.removeAttr("class");
                selectObjs.appendTo(leftListObj);
                dsSelect_take.initEvent(obj);
            }
        });
    },
    "eventGetValueLR":function(obj,lr){
        let listId = obj.listId;
        let lrtemp = ".left";
        if(lr=="r"){
            lrtemp =".right";
        }
        let result=[];
        let selectObjs = $("#"+listId+" "+lrtemp+" .dsList li");
        if(selectObjs==null||selectObjs.length==0){
            return result;
        }
        for(let i=0;i<selectObjs.length;i++){
            let selectItem = selectObjs[i];
            result[result.length] = $(selectItem).data("tempData");
        }
        return result;

    },
    /**
     * 设置数据
     * @param obj
     * @param lr 左右
     * @param datasArray 数据集合
     * @param showParam 要显示的字段
     */
    "setDatasWithLR":function(obj,lr,datasArray,showParam){
        let lrtemp = ".left";
        if(lr=="r"){
            lrtemp =".right";
        }
        let listId = obj.listId;
        let listRightObj = $("#"+listId+" "+lrtemp+" .dsList");
        for(let i=0;i<datasArray.length;i++){
            let dataItem  = datasArray[i];
            let u = dsSelect_tools.guid();
            let appendStr=dsSelect_models.selectItemModel(u,dataItem[showParam]);
            listRightObj.append(appendStr);
            $("#"+listId+" "+lrtemp+" .dsList li[u='"+u+"']").data("tempData",dataItem);
        }
    },
    "guid":function(){
        return (S4()+S4()+"-"+S4()+"-"+S4()+"-"+S4()+"-"+S4()+S4()+S4());
    }
};

function S4() {
    return (((1+Math.random())*0x10000)|0).toString(16).substring(1);
};
