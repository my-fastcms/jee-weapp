var isEmpty = true;
var AREA = {
		RSelect: function(options) {
			var settings = {
				url: null,
				detail:false,
				areaList:'',
				areaIn:''
			};
			$.extend(settings, options);
			$.post(settings.url, {}, function(resp){
				//添加省份
		    	$.each(resp, function(value, name) {
		    		var str= "<li class='quanxian-title province-one'><a class='clearfix js-province'>" +
	                "<span class='open-city col-md-10' value="+value+"><i class='iconfont icon-xiangyoujiantou'></i><span label='"+name+"'>"+name+"</span></span>"+
	                "<span class='col-md-2  city-item city-item1 city-btn"+value+"' id='"+value+"'><i class='iconfont icon-duigou'></i></span>"+
	                "</a><ul class='city"+value+" "+"city' style='display:none'></ul></li>";
		            $('.province').append(str);
		            $(".city-btn"+value).bind("click",value,allSelect);
		    	});
			    //为省份绑定按钮事件
			    $('.province-one .open-city').on('click',function() {
			    	$('.province-one .open-city').css("color","").find("i").removeClass("icon-xiangxiajiantou").addClass("icon-xiangyoujiantou");
			    	addCitySelect($(this).attr("value"));
			        if ($(this).parent().next(".city").css('display') == "none") {
			            //展开未展开
			        	$(this).css("color","#43a8ff").find("i").removeClass("icon-xiangyoujiantou").addClass("icon-xiangxiajiantou");
			            $('.province-one').children('.city').slideUp(300);
			            $(this).parent().next(".city").slideDown(300);
			        } else {
			            //收缩已展开
			            $(this).parent().next(".city").slideUp(300);
			        }
			    });
			    //编辑则回写省数据
			    if(settings.detail){
			    	$(".province").find("span.city-item").each(function(i){
						var cityThis = this;
						$.each(JSON.parse(settings.areaList), function(index,value){
							if($(cityThis).attr("id") == value.provinceId){
								$(cityThis).addClass("activity");
								if(value.cityId.length > 0 ) {
									//$(cityThis).prev().trigger("click");
									$.post(settings.url, {parentId:value.provinceId}, function(resp){
										AREA.addCityHtml(value.provinceId,resp);
							    		//编辑回写市区
						    			$(".city"+value.provinceId).find(".city-item").each(function(i){
						    				var cThis = this
			    							$.each(value.cityId, function(n,val){
			    								if($(cThis).attr("id") == val){
			    									$(cThis).addClass("activity");
			    								}
			    							})
						    			})
						    			//回写是否包含所选区域
						    			$(":radio[name='radio-area'][value='" + settings.areaIn + "']").trigger("click");
						    			settings.detail = false;
						    			//回写完成自动确认
						    			AREA.sureArea();
							    	})
								}
							}
						});
					});
			    }
			})
			
			//添加市
			function addCitySelect(id){
				if($(".city"+id).find("li").length > 0) return;
		    	$.post(settings.url, {parentId:id}, function(resp){
		    		AREA.addCityHtml(id,resp);
		    	})
			}
		    
		    //省 全选
		    function allSelect(element){
		        if(!$(this).hasClass('activity')){
		            $(this).parents(".province-one").find('.city-item').addClass('activity');
		            $(this).addClass('activity');
		        }else{
		            $(this).parents(".province-one").find('.city-item').removeClass('activity');
		            $(this).removeClass('activity');
		        }
		    };
		    
		    
		},
	//添加市的html
	addCityHtml:function(id,data){
		$.each(data, function(value, name) {
    		var cityStr= "<li class='quanxian-title city-two'><a class='clearfix js-city'>" +
            "<span class='open-area col-md-7'>"+
            "<span label='"+name+"'>"+name+"</span></span>"+
            "<span class='col-md-5 city-item city-item3 finaly-btn"+value+"' id='"+value+"'><i class='iconfont icon-duigou'></i></span>"+
            "</a>" +
            "</li>";
    		$(".city"+id).append(cityStr);
            $(".city"+id+" "+".finaly-btn"+value).bind("click",value,AREA.selectedArea);
		})
	},
	//市 单选
    selectedArea:function(element){
        var nodes=[];
        var citys=$(this).parent("a").parent(".city-two").parent('.city').children(".city-two");
        if($(this).hasClass('activity')){
            $(this).removeClass('activity');
        }else{
            $(this).addClass('activity');
            $(this).parents(".province-one").children('a').children('.city-item').addClass('activity');
            $(this).parents(".city-two").children('a').children('.city-item').addClass('activity');
        }
        for(var i=0;i<citys.length;i++){
            if($(citys[i]).children('a').children('.city-item').hasClass('activity')){
                nodes.push(i);
            }
        }
        if(nodes.length===0){
            $(this).parents(".province-one").children('a').children('.city-item').removeClass('activity');
            $(this).parents(".city-two").children('a').children('.city-item').removeClass('activity');
        }
    },
	//获取所选地区
	saveList:function(){
		var areaObj = {};
		var data = [];
		$(".js-province .activity").each(function(){
			var dataObj = {};
			var dataAry=[];
			$(this).parent().next().find(".js-city .activity").each(function(){
				dataAry.push($(this).attr("id"));
			})
			dataObj.provinceId = $(this).attr("id");
			dataObj.cityId = dataAry;
			//把该级数据赋给data数组
			data.push(dataObj)
		});
		if(isEmpty){
			$(".sure-remove-area").find("a").css("color","#ed5565")
			obz.warn("请先确定所属区域");
			return null;
		}
		areaObj.areaIn = $(':radio[name="radio-area"]:checked').val();
		areaObj.areaList = data;
		return areaObj
	},
	//确认所选地区
	sureArea:function(){
		var str = "";
		$(".js-province .activity").each(function(){
			var strPro = $(this).prev().find("span").attr("label");
			var strCity = "";
			$(this).parent().next().find(".js-city .activity").each(function(){
				strCity += $(this).prev().find("span").attr("label") + ",";
			})
			str += '<span style="font-size:14px; display:block;margin-top:10px">'+strPro+':</span>'
			if(strCity != ""){
				str += '<span style="display:block;padding-left:20px;color:#555"> '+strCity+' </span>'
			}
			isEmpty = false;
		});
		if(isEmpty) {obz.warn("最少选择一个所属区域")}
		else{
			$(".sure-remove-area").find("a").css("color","#337ab7");
			$(".province").css("display","none");
			$(".js-sureArea").empty().append(str);
			$(".sure-remove-area").addClass("hide");
			$(".cancel-area").removeClass("hide");
			$(':radio[name="radio-area"]').attr("disabled",true).css("cursor","no-drop");
		};
	},
	//编辑所选地区
	cancelArea:function(){
		isEmpty = true;
		$(".province").css("display","block");
		$(".js-sureArea").empty();
		$(".sure-remove-area").removeClass("hide");
		$(".cancel-area").addClass("hide");
		$(':radio[name="radio-area"]').attr("disabled",false).css("cursor","pointer");
	},
	//重置所选地区
	removeArea:function(){
		$(".province").empty();
		AREA.RSelect({
			url:obz.ctx+"/area/list"
		});
	},
	//编辑时加载地区
	setArea:function(areaList,areaIn,getType){
		$(":radio[name='get_type'][value='" + getType + "']").prop("checked", "checked");
		$(".get_content").append($("#area_tpl").html());
		AREA.RSelect({
			url:obz.ctx+"/area/list",
			detail:true,
			areaList:areaList,
			areaIn:areaIn
			});
	}
}
