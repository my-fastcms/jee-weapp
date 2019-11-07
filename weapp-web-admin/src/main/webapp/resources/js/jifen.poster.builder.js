//Canvas 面板
//pallet 调色板
var PosterConfig = {
	pagewidth : 640,
	pageheight : 900
}

var PosterBuilder = {
		
	init : function(){
		// workaround for IE draggable bug #4333 http://bugs.jqueryui.com/ticket/4333
        $.extend($.ui.sortable.prototype, (function (orig) {
            return {
                _mouseCapture: function (event) {
                    var result = orig.call(this, event);
                    if (result && $.browser.msie) event.stopPropagation();
                    return result;
                }
            };
        })($.ui.sortable.prototype["_mouseCapture"]));
        
		PosterBuilder.initPallet();
		PosterBuilder.initCanvas();
	},	
	
	initPallet : function(){
		$(".form-palette-element").draggable({
            connectToSortable: ".form-container-div",
            helper:'clone',
            zIndex: 200,
            opacity: 0.7,
            revert: "invalid",
            cursor: "move"
        }).disableSelection();
	},
	
	initCanvas : function(){
		$("#form-container-div").css("width", PosterConfig.pagewidth +"px").css("height", PosterConfig.pageheight+"px");

		$('#form-container-div').disableSelection();
		$('#form-container-div').children().each(function(){
		  	PosterBuilder.initElementObj($(this));
		});
	},
	
	//创建一个新的拖拽元素对象后,对该对象进行属性初始化
	//设置样式,绑定相关事件 
	initElementObj : function(element){
		//初始化css为选中,取消其他element选择状态
		PosterBuilder.clear();
    	element.removeClass('unseled').addClass('seled');
    	
    	if(element.is("img")){
    		//可缩放
        	element.resizable({containment:'parent'}).resizable('destroy').resizable({containment:'parent', minWidth: 60, minHeight:60, aspectRatio: 'true'});
    	
        	element.parent().draggable({
    		    containment: '#form-container-div',
    		    scroll: false,
    		    start: function () {
    		    	PosterBuilder.clear();
    		    	element.removeClass('unseled').addClass('seled');
    		    },
    		    stop: function(){
    		    	//element.addClass("expressDataItem");
    		    }
    		});
        	
    	}else{
        	element.draggable({
    		    containment: '#form-container-div',
    		    scroll: false,
    		    start: function () {
    		    	PosterBuilder.clear();
    		    	element.removeClass('unseled').addClass('seled');
    		    },
    		    stop: function(){
    		    	//element.addClass("expressDataItem");
    		    }
    		});
        	
        	if(!element.hasClass("nick_span") && element.find("a").length == 0)
        		element.append('<a class="delX" href="javascript:void(0);" title="删除" onclick="PosterBuilder.delElement(this)"></a>');

    	}
    	
		//绑定点击事件
		element.bind('click', function () {
			PosterBuilder.clear();
		    element.removeClass('unseled').addClass('seled');
		    $("#sel_FontNameList,#sel_FontSizeList,#chk_PrintBorderCuti_Lab").attr("disabled", false);
		    
		    if(element.is("span")){
		    	$("#edit_area_div").show();
		    	$("#text_content_input").val(element.text());
		    	if(element.hasClass("nick_span")){
		    		$("#text_content_input").hide();
		    	}else{
		    		$("#text_content_input").show();
		    	}
		    }else{
		    	$("#edit_area_div").hide();
		    }
		});
	},
	
	delElement : function(element){
		obz.showMessage("确认删除吗?删除记得点击最下方保存按钮后，才能生效", function(){
			var a = $(element).parent();
			if(a) {
				a.remove();
			}
			return false;
		});
	},
	
	//设置快递单背景图
	setBgimg : function(selImgs){
		if(selImgs.length<=0){
			obz.error("请选择一张图片");
			return false;
		}
		var img = selImgs[0];
		$("#posterbgmig").val(img.url);
		$("#form-container-div").css('background-image', 'url(' + img.url + ')');
		return true;
	},
	
	clear : function(){
		$('#form-container-div>*').removeClass('seled').addClass('unseled');
	}
		
}; 
