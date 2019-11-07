//任务宝海报设计器 wangjun
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
        	
        	if(!element.hasClass("invitecode_span") && !element.hasClass("nick_span") && element.find("a").length == 0)
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
	
	//保存
	saveDesignHtml : function(){
		
		obz.showMessage("确定保存吗?", function(){
			var tplcontent="";//只取拖拽后生成的原始模板内容，排除div跟背景图
			$("#form-container-div").children().each(function(){
				var me = $(this);
				if(me.is("div")){
					me.children("img").each(function(){
						var imgobj = $(this).clone(true);
						imgobj.attr("style", me.attr("style"));
						tplcontent += imgobj.prop("outerHTML");
					});
				}else{
					tplcontent += me.prop("outerHTML");
				}
			});
			
			log("===========designHtml:" + tplcontent)
			
			var params = {};
			var id = $("#postId").val();
			var bgimg = $("#posterbgmig").val();
			if(bgimg == null || bgimg == ""){
				obz.error("请设置海报背景图");
				return;
			}
//			if($("#reply_keyword").val() == null || $("#reply_keyword").val() == ""){
//				obz.error("请设置获取个人专属海报关键字");
//				return;
//			}
			if($("#expire").val() == null || $("#expire").val() == ""){
				obz.error("请设置海报二维码时效");
				return;
			}
			/*if($("#prompt_msg").val() == null || $("#prompt_msg").val() == ""){
				obz.error("请设置消息提示");
				return;
			}*/
			
			//隐藏头像
			params.showHeader = $('#show_header').is(':checked');
			//隐藏昵称
			params.showNick = $('#show_nick').is(':checked');
			params.active = $('#chk_active').is(':checked');
//			params.isFollowsCreate = $('#isFollowsCreate').is(':checked');
			
			params.id = id;
			params.tplcontent = tplcontent;
			params.bgimg = bgimg;
			params.replyKeyword = $("#reply_keyword").val();
//			params.awardKeyword = $("#award_keyword").val();
//			params.promptMsg = $("#prompt_msg").val();
//			params.newsImage = $("#news_image").val();
			params.expire = $("#expire").val();
			$("#builder-container").mask("正在保存...");
			obz.ajaxJson(obz.ctx + "/task/poster/save", params, function(resp){
				$("#builder-container").unmask();
				if(resp.state == "ok"){
					obz.msg("设置成功", function(){
						location.href=obz.ctx + "/task/poster";
					});
				}
			});
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
