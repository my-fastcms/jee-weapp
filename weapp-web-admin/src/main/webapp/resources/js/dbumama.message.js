var WxmMsg = {

	//添加微信昵称	
	insertNick:function(obj){
			$(obj).prev().insertAtCaret("#微信昵称#");
	}
	,
		
	// 删除已选择的素材
	delNewsRow : function(obj) {
		$(obj).parent().remove();
	}

	// 删除一行回复
	,
	delRow : function(obj) {
		$(obj).parent().parent().remove();
	}

	// 增加一行回复
	,
	addRow : function(obj) {
		$("#" + obj).append($("#reply_type_tpl").html());
	}

	,
	parseToNewsEntity_ : function(select_content_div, obj, type){
		if ($(obj).parent().parent().find("div." + select_content_div).attr("data-type") != 5) {
			$(obj).parent().parent().find("div." + select_content_div).children().remove();
		}
		
		var count = $(obj).parent().parent().find("div." + select_content_div).find("div.newsmsg_item").length;
		log("count:" + count);
		var newCount = TBatch.getCheckedCount();
		if (newCount + count > 1) {
			obz.info("微信规定一条图文消息不能超过1个素材");
			return false;
		}
		
		$(obj).parent().parent().find("div."+select_content_div).attr("data-type", "5");
		$(obj).parent().parent().find("div."+select_content_div).find("span").empty();
		var idsArr = TBatch.getChecked().split("-");
		for (var i = 0; i < idsArr.length; i++) {
			var id = idsArr[i];
			if (id != null && id != "") {
				var tr = $("#tr_id_" + id);
				var entity = new Object();
				entity.msgTitle = tr.attr("data-title");
				entity.msgDesc = tr.attr("data-digest");
				entity.msgUrl = tr.attr("data-url");
				entity.id = tr.attr("data-id");
				entity.msgPic = tr.attr("data-image");
				entity.msgOrgType = type;// 数据来源，是商品，还是微信素材，还是其他
				$(obj).parent().parent().find("div."+select_content_div).append(template("wechat_media_sel_tpl", entity));
			}
		}
		
		return true;
	}
	
	,
	parseToNewsEntity : function(obj, type) {
		var me = this;
		return me.parseToNewsEntity_("select_content", obj, type);
//		if ($(obj).parent().parent().find("div.select_content").attr(
//				"data-type") != 5) {
//			$(obj).parent().parent().find("div.select_content").children()
//					.remove();
//		}
//
//		var count = $(obj).parent().parent().find("div.select_content").find(
//				"div.newsmsg_item").length;
//		log("count:" + count);
//		var newCount = TBatch.getCheckedCount();
//		if (newCount + count > 1) {
//			obz.info("微信规定一条图文消息不能超过1个素材");
//			return false;
//		}
//
//		$(obj).parent().parent().find("div.select_content").attr("data-type",
//				"5");
//		$(obj).parent().parent().find("div.select_content").find("span")
//				.empty();
//		var idsArr = TBatch.getChecked().split("-");
//		for (var i = 0; i < idsArr.length; i++) {
//			var id = idsArr[i];
//			if (id != null && id != "") {
//				var tr = $("#tr_id_" + id);
//				var entity = new Object();
//				entity.msgTitle = tr.attr("data-title");
//				entity.msgDesc = tr.attr("data-digest");
//				entity.msgUrl = tr.attr("data-url");
//				entity.id = tr.attr("data-id");
//				entity.msgPic = tr.attr("data-image");
//				entity.msgOrgType = type;// 数据来源，是商品，还是微信素材，还是其他
//				$(obj).parent().parent().find("div.select_content").append(
//						template("wechat_media_sel_tpl", entity));
//			}
//		}
//
//		return true;
	}

	,
	textClick_ : function(select_content_div, obj){
		$(obj).parent().parent().find("div."+select_content_div).attr("data-type", "0");
		$(obj).parent().parent().find("div."+select_content_div).empty().append($("#text_tpl").html());
	}
	
	// 选择文本事件
	,
	textClick : function(obj) {
		var me = this;
		return me.textClick_("select_content", obj);
//		$(obj).parent().parent().find("div.select_content").attr("data-type", "0");
//		$(obj).parent().parent().find("div.select_content").empty().append($("#text_tpl").html());
	}

	,
	openProductDialog_ : function(select_content_div, obj){
		var url = obz.ctx + "/product/youzan";
		var me = this;
		BootstrapDialog.show({
			size : BootstrapDialog.SIZE_WIDE,
			title : "选择商品",
			message : $('<div></div>').load(url),
			buttons : [ {
				label : '关闭',
				action : function(dialogItself) {
					dialogItself.close();
				}
			}, {
				label : '确定',
				cssClass : "btn-primary",
				action : function(self) {
					var count = TBatch.getCheckedCount();
					if (count <= 0) {
						obz.warn("请选择商品")
						return;
					}
					if (me.parseToNewsEntity_(select_content_div, obj, 1))
						self.close();
				}
			} ]
		});
		return false;
	}
	
	// 选择有赞商品对话框
	,
	openProductDialog : function(obj) {
		var me = this;
		return me.openProductDialog_("select_content", obj);
//		var url = obz.ctx + "/product/youzan";
//		var me = this;
//		BootstrapDialog.show({
//			size : BootstrapDialog.SIZE_WIDE,
//			title : "选择商品",
//			message : $('<div></div>').load(url),
//			buttons : [ {
//				label : '关闭',
//				action : function(dialogItself) {
//					dialogItself.close();
//				}
//			}, {
//				label : '确定',
//				cssClass : "btn-primary",
//				action : function(self) {
//					var count = TBatch.getCheckedCount();
//					if (count <= 0) {
//						obz.warn("请选择商品")
//						return;
//					}
//					if (me.parseToNewsEntity(obj, 1))
//						self.close();
//				}
//			} ]
//		});
//		return false;
	}

	,
	openImageClick_ : function(select_content_div){
		var me = this;
		obz.selectImageOne(function(selImgs) {

			if (selImgs.length > 0) {
				var imgObj = selImgs[0];
				return me.parseToImageEntity_(select_content_div, obj, imgObj);
			}

		});
		return false;
	}

	/* 选择图片事件 */
	,
	openImageClick : function(obj) {
		var me = this;
		obz.selectImageOne(function(selImgs) {

			if (selImgs.length > 0) {
				var imgObj = selImgs[0];
				return me.parseToImageEntity(obj, imgObj);
			}

		});
		return false;
	}

	,
	parseToImageEntity_ : function(select_content_div, obj, img){
		// 清除已有素材的内容
		$(obj).parent().parent().find("div."+select_content_div).children().remove();
		$(obj).parent().parent().find("div."+select_content_div).attr("data-type", "1");

		var entity = {};
		entity.mediaId = img.mediaId;
		entity.msgPic = img.url;
		if (entity.mediaId == null || entity.mediaId == "") {
			obz.error("该图片无法发送消息，请先上传到微信服务器");
			return false;
		}

		$(obj).parent().parent().find("div."+select_content_div).append(
				template("image_sel_tpl", entity));
		return true;
	}
	
	/* 选择图片事件 */
	,
	parseToImageEntity : function(obj, img) {
		var me = this;
		return me.parseToImageEntity_("select_content", obj, img);
		// 清除已有素材的内容
//		$(obj).parent().parent().find("div.select_content").children().remove();
//		$(obj).parent().parent().find("div.select_content").attr("data-type",
//				"1");
//
//		var entity = {};
//		entity.mediaId = img.mediaId;
//		entity.msgPic = img.url;
//		if (entity.mediaId == null || entity.mediaId == "") {
//			obz.error("该图片无法发送消息，请先上传到微信服务器");
//			return false;
//		}
//
//		$(obj).parent().parent().find("div.select_content").append(
//				template("image_sel_tpl", entity));
//		return true;
	}
	
	
	,
	routineClick_ : function(select_content_div, obj){
		$(obj).parent().parent().find("div."+select_content_div).attr("data-type", "8");
		$(obj).parent().parent().find("div."+select_content_div).empty().append($("#routine_sel_tpl").html());
	}

	/* 选择小程序事件 */
	,
	routineClick : function(obj) {
		var me = this;
		return me.routineClick_("select_content", obj);
//		$(obj).parent().parent().find("div.select_content").attr("data-type",
//				"8");
//		$(obj).parent().parent().find("div.select_content").empty().append(
//				$("#routine_sel_tpl").html());
	}

	,
	routineImageClick_ : function(select_content_div, obj){
		var me = this;
		obz.selectImageOne(function(selImgs) {

			if (selImgs.length > 0) {
				var imgObj = selImgs[0];
				return me.parseToRoutineEntity_(select_content_div, obj, imgObj);
			}

		});

		return false;
	}
	
	/* 选择小程序图片事件 */
	,
	routineImageClick : function(obj) {
		var me = this;
		obz.selectImageOne(function(selImgs) {

			if (selImgs.length > 0) {
				var imgObj = selImgs[0];
				return me.parseToRoutineEntity(obj, imgObj);
			}

		});

		return false;
	}

	,
	parseToRoutineEntity_ : function(select_content_div, obj, img){
		// 清除已有素材的内容
		$(obj).parent().find("div.imagemsg_item").remove();

		var entity = {};
		entity.mediaId = img.mediaId;
		entity.msgPic = img.url;
		if (entity.mediaId == null || entity.mediaId == "") {
			obz.error("该图片无法发送消息，请先上传到微信服务器");
			return false;
		}

		$(obj).parent().parent().find("div." + select_content_div).append(template("image_sel_tpl", entity));

		return true;
	}
	
	/* 选择小程序图片事件 */
	,
	parseToRoutineEntity : function(obj, img) {
		var me = this;
		return me.parseToRoutineEntity_("select_content", obj, img);
		// 清除已有素材的内容
//		$(obj).parent().find("div.imagemsg_item").remove();
//
//		var entity = {};
//		entity.mediaId = img.mediaId;
//		entity.msgPic = img.url;
//		if (entity.mediaId == null || entity.mediaId == "") {
//			obz.error("该图片无法发送消息，请先上传到微信服务器");
//			return false;
//		}
//
//		$(obj).parent().parent().find("div.select_content").append(
//				template("image_sel_tpl", entity));
//
//		return true;
	}

	,
	openWechatMediaDialog_ : function(select_content_div, obj){
		var url = obz.ctx + "/media/wechat";
		var me = this;
		BootstrapDialog.show({
			size : BootstrapDialog.SIZE_WIDE,
			title : "选择微信图文素材",
			message : $('<div></div>').load(url),
			buttons : [ {
				label : '关闭',
				action : function(dialogItself) {
					dialogItself.close();
				}
			}, {
				label : '确定',
				cssClass : "btn-primary",
				action : function(self) {
					var count = TBatch.getCheckedCount();
					if (count <= 0) {
						obz.warn("请选择微信图文素材")
						return;
					}
					if (me.parseToNewsEntity_(select_content_div, obj, 2))
						self.close();
				}
			} ]
		});
		return false;
	}
	
	,
	openWechatMediaDialog : function(obj) {
		var me = this;
		return me.openWechatMediaDialog_("select_content", obj);
//		var url = obz.ctx + "/media/wechat";
//		var me = this;
//		BootstrapDialog.show({
//			size : BootstrapDialog.SIZE_WIDE,
//			title : "选择微信图文素材",
//			message : $('<div></div>').load(url),
//			buttons : [ {
//				label : '关闭',
//				action : function(dialogItself) {
//					dialogItself.close();
//				}
//			}, {
//				label : '确定',
//				cssClass : "btn-primary",
//				action : function(self) {
//					var count = TBatch.getCheckedCount();
//					if (count <= 0) {
//						obz.warn("请选择微信图文素材")
//						return;
//					}
//					if (me.parseToNewsEntity(obj, 2))
//						self.close();
//				}
//			} ]
//		});
//		return false;
	}

	,
	openCustomMediaDialog_ : function(select_content_div, obj){
		var url = obz.ctx + "/media/index";
		var me = this;
		BootstrapDialog.show({
			size : BootstrapDialog.SIZE_WIDE,
			title : "选择自定义图文素材",
			message : $('<div></div>').load(url),
			buttons : [ {
				label : '关闭',
				action : function(dialogItself) {
					dialogItself.close();
				}
			}, {
				label : '确定',
				cssClass : "btn-primary",
				action : function(self) {
					var count = TBatch.getCheckedCount();
					if (count <= 0) {
						obz.warn("请选择自定义图文素材")
						return;
					}
					if (me.parseToNewsEntity_(select_content_div, obj, 3))
						self.close();
				}
			} ]
		});
		return false;
	}
	
	,
	openCustomMediaDialog : function(obj) {
		var me = this;
		return me.openCustomMediaDialog_("select_content", obj);
//		var url = obz.ctx + "/media/index";
//		var me = this;
//		BootstrapDialog.show({
//			size : BootstrapDialog.SIZE_WIDE,
//			title : "选择自定义图文素材",
//			message : $('<div></div>').load(url),
//			buttons : [ {
//				label : '关闭',
//				action : function(dialogItself) {
//					dialogItself.close();
//				}
//			}, {
//				label : '确定',
//				cssClass : "btn-primary",
//				action : function(self) {
//					var count = TBatch.getCheckedCount();
//					if (count <= 0) {
//						obz.warn("请选择自定义图文素材")
//						return;
//					}
//					if (me.parseToNewsEntity(obj, 3))
//						self.close();
//				}
//			} ]
//		});
//		return false;
	}

	,
	setContent_ : function(select_content_div, contentCfg){
		if (typeof (contentCfg.id) == "undefined") {
			// 客服消息设置情况
			contentCfg.msgType = contentCfg.msg_type;
			contentCfg.id = "kefu";
		}

		if(select_content_div == "config-list"){
			$("#"+select_content_div).append(template("reply_type_tpl", contentCfg));	
		}else{
			$("#"+select_content_div).append(template("reply_type_tpl_unsub", contentCfg));			
		}

		if (contentCfg.msgType == 0) {
			// 纯文本消息
			$("#sel_div_" + contentCfg.id).append($("#text_tpl").html());
			$("#sel_div_" + contentCfg.id).find("textarea").val(
					contentCfg.msg_text_content || contentCfg.msgTextContent);
		} else if (contentCfg.msgType == 1) {
			// 群发图片
			var entity = {};
			entity.mediaId = contentCfg.msg_media_id || contentCfg.mediaId;
			entity.msgPic = contentCfg.msg_media_pic || contentCfg.mediaPic;
			$("#sel_div_" + contentCfg.id).append(
					template("image_sel_tpl", entity));
		} else if (contentCfg.msgType == 8) {
			// 群发小程序
			$("#sel_div_" + contentCfg.id).append($("#routine_sel_tpl").html());
			$("#sel_div_" + contentCfg.id).find("input#app_id").val(
					contentCfg.appId || contentCfg.app_id);
			$("#sel_div_" + contentCfg.id).find("input#app_path").val(
					contentCfg.appPath || contentCfg.app_path);
			$("#sel_div_" + contentCfg.id).find("input#app_title").val(
					contentCfg.title || contentCfg.app_title);
			var entity = {};
			entity.mediaId = contentCfg.msg_media_id || contentCfg.mediaId;
			entity.msgPic = contentCfg.msg_media_pic || contentCfg.mediaPic;
			$("#sel_div_" + contentCfg.id).append(
					template("image_sel_tpl", entity));
		} else if (contentCfg.msgType == 5) {
			// 图文消息
			var replyNews = contentCfg.replyNews;
			for (var k = 0; k < replyNews.length; k++) {
				var replyNew = replyNews[k];
				// 商品
				var entity = new Object();
				entity.msgTitle = replyNew.msg_title || replyNew.msgTitle;
				entity.msgDesc = replyNews.msg_desc || replyNew.msgDesc;
				entity.msgUrl = replyNew.msg_url || replyNew.msgUrl;
				entity.msgPic = replyNew.msg_pic || replyNew.msgPic;
				entity.msgOrgType = replyNew.msg_org_type
						|| replyNew.msgOrgType;// 数据来源，是商品，还是微信素材，还是其他
				$("#sel_div_" + contentCfg.id).append(
						template("wechat_media_sel_tpl", entity));
			}
		}
	}
	
	,
	setContent : function(contentCfg) {
		var me = this;
		return me.setContent_("config-list", contentCfg);
		
//		if (typeof (contentCfg.id) == "undefined") {
//			// 客服消息设置情况
//			contentCfg.msgType = contentCfg.msg_type;
//			contentCfg.id = "kefu";
//		}
//
//		$("#config-list").append(template("reply_type_tpl", contentCfg));
//
//		if (contentCfg.msgType == 0) {
//			// 纯文本消息
//			$("#sel_div_" + contentCfg.id).append($("#text_tpl").html());
//			$("#sel_div_" + contentCfg.id).find("textarea").val(
//					contentCfg.msg_text_content || contentCfg.msgTextContent);
//		} else if (contentCfg.msgType == 1) {
//			// 群发图片
//			var entity = {};
//			entity.mediaId = contentCfg.msg_media_id || contentCfg.mediaId;
//			entity.msgPic = contentCfg.msg_media_pic || contentCfg.mediaPic;
//			$("#sel_div_" + contentCfg.id).append(
//					template("image_sel_tpl", entity));
//		} else if (contentCfg.msgType == 8) {
//			// 群发小程序
//			$("#sel_div_" + contentCfg.id).append($("#routine_sel_tpl").html());
//			$("#sel_div_" + contentCfg.id).find("input#app_id").val(
//					contentCfg.appId || contentCfg.app_id);
//			$("#sel_div_" + contentCfg.id).find("input#app_path").val(
//					contentCfg.appPath || contentCfg.app_path);
//			$("#sel_div_" + contentCfg.id).find("input#app_title").val(
//					contentCfg.title || contentCfg.app_title);
//			var entity = {};
//			entity.mediaId = contentCfg.msg_media_id || contentCfg.mediaId;
//			entity.msgPic = contentCfg.msg_media_pic || contentCfg.mediaPic;
//			$("#sel_div_" + contentCfg.id).append(
//					template("image_sel_tpl", entity));
//		} else if (contentCfg.msgType == 5) {
//			// 图文消息
//			var replyNews = contentCfg.replyNews;
//			for (var k = 0; k < replyNews.length; k++) {
//				var replyNew = replyNews[k];
//				// 商品
//				var entity = new Object();
//				entity.msgTitle = replyNew.msg_title || replyNew.msgTitle;
//				entity.msgDesc = replyNews.msg_desc || replyNew.msgDesc;
//				entity.msgUrl = replyNew.msg_url || replyNew.msgUrl;
//				entity.msgPic = replyNew.msg_pic || replyNew.msgPic;
//				entity.msgOrgType = replyNew.msg_org_type
//						|| replyNew.msgOrgType;// 数据来源，是商品，还是微信素材，还是其他
//				$("#sel_div_" + contentCfg.id).append(
//						template("wechat_media_sel_tpl", entity));
//			}
//		}
	}
	
	,
	getContent_ : function(select_content_div){
		// 获取设置的回复内容数据
		var configArray = new Array();
		var errorArray = new Array();

		log($("div."+select_content_div).length);
		$("div."+select_content_div).each(
				function() {
					var msgType = $(this).attr("data-type");
					if (!msgType || msgType == "{{msgType}}") {
						var errorObj = new Object();
						errorObj.error = "请设置回复消息内容";
						errorArray.push(errorObj);
						return null;
					}

					var configObj = new Object();
					configObj.msg_type = msgType;

					if (msgType == "0") {// 纯文本消息
						var textValue = $(this).find("textarea").val();

						if (textValue == "" || textValue == null) {
							var errorObj = new Object();
							errorObj.error = "纯文本消息不能为空";
							errorArray.push(errorObj);
						} else {
							configObj.msg_text_content = textValue;
						}

					} else if (msgType == "1") {// 图片
						if (!$(this).find("div.imagemsg_item").attr(
								"data-media-id")) {
							var errorObj = new Object();
							errorObj.error = "图片不能为空";
							errorArray.push(errorObj);
						} else {
							configObj.media_id = $(this).find(
									"div.imagemsg_item").attr("data-media-id");
							configObj.media_pic = $(this).find(
									"div.imagemsg_item").attr("data-image");
						}
					} else if (msgType == "5") {// 图文消息
						var replyNewsArr = new Array(); // 图文消息行配置，一天消息可以包含最多8条图文消息
						// 获取菜单设置的图文消息数据
						$(this).find("div.newsmsg_item").each(
								function() {
									log("===title:"
											+ $(this).attr("data-title"));
									log("===url:" + $(this).attr("data-url"));
									log("===image:"
											+ $(this).attr("data-image"));
									var replyNewsObj = new Object();
									replyNewsObj.msg_title = $(this).attr(
											"data-title");
									replyNewsObj.msg_desc = $(this).attr(
											"data-desc");
									replyNewsObj.msg_pic = $(this).attr(
											"data-image");
									replyNewsObj.msg_url = $(this).attr(
											"data-url");
									replyNewsObj.msg_org_type = $(this).attr(
											"data-type");
									replyNewsArr.push(replyNewsObj);
								});

						if (replyNewsArr.length <= 0) {
							var errorObj = new Object();
							errorObj.error = "图文消息最少要有一条记录";
							errorArray.push(errorObj);
						} else {
							configObj.replyNews = replyNewsArr;
						}
					} else if (msgType == "8") {// 小程序
						var appId = $(this).find("input#app_id").val();
						var appPath = $(this).find("input#app_path").val();
						var title = $(this).find("input#app_title").val();
						var hasError = false;
						if (appId == "" || appId == null) {
							var errorObj = new Object();
							errorObj.error = "小程序Appid不能为空";
							errorArray.push(errorObj);
							hasError = true;
						}
						if (appPath == "" || appPath == null) {
							var errorObj = new Object();
							errorObj.error = "小程序访问路径不能为空";
							errorArray.push(errorObj);
							hasError = true;
						}
						if (title == "" || title == null) {
							var errorObj = new Object();
							errorObj.error = "小程序标题不能为空";
							errorArray.push(errorObj);
							hasError = true;
						}
						if (!$(this).find("div.imagemsg_item").attr(
								"data-media-id")
								|| !$(this).find("div.imagemsg_item").attr(
										"data-image")) {
							var errorObj = new Object();
							errorObj.error = "小程序封面图不能为空";
							errorArray.push(errorObj);
							hasError = true;
						}

						if (!hasError) {
							configObj.app_id = appId;
							configObj.app_path = appPath;
							configObj.title = title;
							configObj.thumb_media_id = $(this).find(
									"div.imagemsg_item").attr("data-media-id");
							configObj.thumb_media_pic = $(this).find(
									"div.imagemsg_item").attr("data-image");
							log("====appId:" + configObj.app_id
									+ "====appPath:" + appPath + "====title:"
									+ title);
						}
					}
					configArray.push(configObj);
				});
		
		if($("div."+select_content_div).length <= 0){
			var errorObj = new Object();
			errorObj.error = "请设置回复消息内容";
			errorArray.push(errorObj);
		}
		if (errorArray.length > 0) {
			var errorMsg = "";
			for (var i = 0; i < errorArray.length; i++) {
				var error = errorArray[i];
				errorMsg += error.error + "</br>";
			}
			obz.warn(errorMsg);
			return null;
		}

		return configArray;
	}

	,
	getContent : function() {
		var me = this;
		return me.getContent_("select_content");
		// 获取设置的回复内容数据
//		var configArray = new Array();
//		var errorArray = new Array();
//
//		log($("div.select_content").length);
//		$("div.select_content").each(
//				function() {
//					var msgType = $(this).attr("data-type");
//					if (!msgType || msgType == "{{msgType}}") {
//						var errorObj = new Object();
//						errorObj.error = "请设置回复消息内容";
//						errorArray.push(errorObj);
//						return null;
//					}
//
//					var configObj = new Object();
//					configObj.msg_type = msgType;
//
//					if (msgType == "0") {// 纯文本消息
//						var textValue = $(this).find("textarea").val();
//
//						if (textValue == "" || textValue == null) {
//							var errorObj = new Object();
//							errorObj.error = "纯文本消息不能为空";
//							errorArray.push(errorObj);
//						} else {
//							configObj.msg_text_content = textValue;
//						}
//
//					} else if (msgType == "1") {// 图片
//						if (!$(this).find("div.imagemsg_item").attr(
//								"data-media-id")) {
//							var errorObj = new Object();
//							errorObj.error = "图片不能为空";
//							errorArray.push(errorObj);
//						} else {
//							configObj.media_id = $(this).find(
//									"div.imagemsg_item").attr("data-media-id");
//							configObj.media_pic = $(this).find(
//									"div.imagemsg_item").attr("data-image");
//						}
//					} else if (msgType == "5") {// 图文消息
//						var replyNewsArr = new Array(); // 图文消息行配置，一天消息可以包含最多8条图文消息
//						// 获取菜单设置的图文消息数据
//						$(this).find("div.newsmsg_item").each(
//								function() {
//									log("===title:"
//											+ $(this).attr("data-title"));
//									log("===url:" + $(this).attr("data-url"));
//									log("===image:"
//											+ $(this).attr("data-image"));
//									var replyNewsObj = new Object();
//									replyNewsObj.msg_title = $(this).attr(
//											"data-title");
//									replyNewsObj.msg_desc = $(this).attr(
//											"data-desc");
//									replyNewsObj.msg_pic = $(this).attr(
//											"data-image");
//									replyNewsObj.msg_url = $(this).attr(
//											"data-url");
//									replyNewsObj.msg_org_type = $(this).attr(
//											"data-type");
//									replyNewsArr.push(replyNewsObj);
//								});
//
//						if (replyNewsArr.length <= 0) {
//							var errorObj = new Object();
//							errorObj.error = "图文消息最少要有一条记录";
//							errorArray.push(errorObj);
//						} else {
//							configObj.replyNews = replyNewsArr;
//						}
//					} else if (msgType == "8") {// 小程序
//						var appId = $(this).find("input#app_id").val();
//						var appPath = $(this).find("input#app_path").val();
//						var title = $(this).find("input#app_title").val();
//						var hasError = false;
//						if (appId == "" || appId == null) {
//							var errorObj = new Object();
//							errorObj.error = "小程序Appid不能为空";
//							errorArray.push(errorObj);
//							hasError = true;
//						}
//						if (appPath == "" || appPath == null) {
//							var errorObj = new Object();
//							errorObj.error = "小程序访问路径不能为空";
//							errorArray.push(errorObj);
//							hasError = true;
//						}
//						if (title == "" || title == null) {
//							var errorObj = new Object();
//							errorObj.error = "小程序标题不能为空";
//							errorArray.push(errorObj);
//							hasError = true;
//						}
//						if (!$(this).find("div.imagemsg_item").attr(
//								"data-media-id")
//								|| !$(this).find("div.imagemsg_item").attr(
//										"data-image")) {
//							var errorObj = new Object();
//							errorObj.error = "小程序封面图不能为空";
//							errorArray.push(errorObj);
//							hasError = true;
//						}
//
//						if (!hasError) {
//							configObj.app_id = appId;
//							configObj.app_path = appPath;
//							configObj.title = title;
//							configObj.thumb_media_id = $(this).find(
//									"div.imagemsg_item").attr("data-media-id");
//							configObj.thumb_media_pic = $(this).find(
//									"div.imagemsg_item").attr("data-image");
//							log("====appId:" + configObj.app_id
//									+ "====appPath:" + appPath + "====title:"
//									+ title);
//						}
//					}
//					configArray.push(configObj);
//				});
//		
//		if($("div.select_content").length <= 0){
//			var errorObj = new Object();
//			errorObj.error = "请设置回复消息内容";
//			errorArray.push(errorObj);
//		}
//		if (errorArray.length > 0) {
//			var errorMsg = "";
//			for (var i = 0; i < errorArray.length; i++) {
//				var error = errorArray[i];
//				errorMsg += error.error + "</br>";
//			}
//			obz.error(errorMsg);
//			return null;
//		}
//
//		return configArray;
	}
	
	,
	createSortable_ : function(config_list){
		var menuList = document.getElementById(config_list);
		var sort = Sortable.create(menuList, {
			animation: 150, // ms, animation speed moving items when sorting, `0` — without animation
			  handle: ".module", // Restricts sort start click/touch to the specified element
			  draggable: ".module", // Specifies which items inside the element should be sortable
			  ghostClass:"js-module",
			  onUpdate: function (evt){
			     var item = evt.item; // the current dragged HTMLElement
			  }
		});
		$(".msg_sender_msg").append('<span style="margin-left: 10px" class="alert-success">拖动单个回复内容可进行内容排序</span>')
	} 
	
	,
	createSortable: function(){
		var me = this;
		me.createSortable_("config-list");
//		var menuList = document.getElementById("config-list");
//		var sort = Sortable.create(menuList, {
//			animation: 150, // ms, animation speed moving items when sorting, `0` — without animation
//			  handle: ".module", // Restricts sort start click/touch to the specified element
//			  draggable: ".module", // Specifies which items inside the element should be sortable
//			  ghostClass:"js-module",
//			  onUpdate: function (evt){
//			     var item = evt.item; // the current dragged HTMLElement
//			  }
//		});
//		$(".msg_sender_msg").append('<span style="margin-left: 10px" class="alert-success">拖动单个回复内容可进行内容排序</span>')
	}
	
	// 任务管理--选择有赞商品对话框
	,
	openProductYouzanDialog : function(obj) {
		var url = obz.ctx + "/prize/type/youzanList";

		var me = this;
		BootstrapDialog.show({
			size : BootstrapDialog.SIZE_WIDE,
			title : "选择商品",
			message : $('<div></div>').load(url),
			buttons : [ {
				label : '关闭',
				action : function(dialogItself) {
					dialogItself.close();
				}
			}, {
				label : '确定',
				cssClass : "btn-primary",
				action : function(self) {
					var count = $("input[name='radio']:checked").size();
					if (count <= 0) {
						obz.warn("请选择商品!")
						return;
					}
					//addPrizeDailog.close();   
					
					
					var selectYouZanId=$("input[name='radio']:checked").attr("id").replace("checkbox_","");
					var selectContent=$("input[name='radio']:checked").parent().parent().attr("data-title");
					var selectPrizeImg=$("input[name='radio']:checked").parent().parent().attr("data-image");

					$("#selectYouZanId").val(selectYouZanId);
					$("#selectContent").val(selectContent); 
					$("#prize_img2").find("img").attr("src",selectPrizeImg); 
					
					$("#prize_img2").attr("style","display: block;");
					
					self.close();

				}
			} ]
		});
		return false;
	}
	
	// 后台任务管理--选择自营商品对话框
	,
	openProductsDialog : function(obj) {
		var url = obz.ctx + "/prize/type/showProducts";

		var me = this;
		BootstrapDialog.show({
			size : BootstrapDialog.SIZE_WIDE, 
			title : "选择商品",
			message : $('<div></div>').load(url),
			buttons : [ {
				label : '关闭',
				action : function(dialogItself) {
					dialogItself.close();
				}  
			}, {
				label : '确定',
				cssClass : "btn-primary",
				action : function(self) {
					var count = $("input[name='radio']:checked").size();
					if (count <= 0) {
						obz.warn("请选择商品!")
						return;
					}
					
					var selectProductId = $("input[name='radio']:checked").parent().next().find("span").attr("id").replace("prizeName_",""); 
					var selectPrizeName = $("input[name='radio']:checked").parent().next().find("span").html();  
					var selectPrizeImg = $("input[name='radio']:checked").parent().find("input[name='hiddenImgUrl']").val(); 
					
					$("#selectPrizeName").val(selectPrizeName);
					
					$("#prize_img").find("img").attr("src",selectPrizeImg);
					$("#hiddenImgUrl").val(selectPrizeImg);
					
					$("#out_id").val(selectProductId);
					
					$("#prize_img").attr("style","display: block;");
					
					self.close();
					
					
				}
			} ]
		});
		return false;
	}
	
}