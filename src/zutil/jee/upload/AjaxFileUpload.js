/* Values:
 * 	Servlet url(String) = SERVLET_URL
 * 	Background upload(boolean) = BGUPLOAD
 * 	Queue item HTML(String) = PROGHTML
 */
var upload_index = 0;
var upload_update = false;

// Autostart
jQuery(document).ready(function(){
	initUpload();
	updateUploadStatus()
});

/* Initiates a new upload */
function initUpload(){
	var name = "uploadFrame_"+upload_index;

	// Add iframe
	jQuery("body").append("<iframe id='"+name+"' name='"+name+"' height='0' " +
			"width='0' frameborder='0' scrolling='yes' src='about:blank'></iframe>");

	// Init form settings
	var form = jQuery("#AjaxFileUpload");
	//form.attr("encoding", "multipart/form-data");
	form.attr("enctype", "multipart/form-data");
	form.attr("method", "post");
	form.attr("target", name);
	form.attr("action", "{SERVLET_URL}");
	form.bind('submit', startUpload );
	//form.attr("onSubmit", "startUpload()");

	// reset the form
	jQuery("#AjaxFileUpload").each(function(){
        this.reset();
	});

	upload_index++;
}

function startUpload(){
	if(!upload_update)
		setTimeout("updateUploadStatus()", 500);

	// Init new upload
	setTimeout("initUpload()", 500);
}


function updateUploadStatus(){
	jQuery.ajax({
		url: "{SERVLET_URL}",
		cache: false,
		dataType: 'json',
		success: function(data){
			// Update
			upload_update = true;
			if(data == null || data.length == 0){
				upload_update = false;
			}
			else setTimeout("updateUploadStatus()", 1000);

			// Request upload info
			jQuery.each(data, function(index,item){
				// add new list item if needed
				if( jQuery("#UploadQueue #"+item.id).size() == 0){
					$("#UploadQueue").append("<li id='"+item.id+"'>{PROGHTML}</li>");
				}
				// Update data
				if(jQuery("#UploadQueue #"+item.id+" .status").size() > 0)
					jQuery("#UploadQueue #"+item.id+" .status").html( item.status );
				if(jQuery("#UploadQueue #"+item.id+" .message").size() > 0)
					jQuery("#UploadQueue #"+item.id+" .message").html( item.message );
				if(jQuery("#UploadQueue #"+item.id+" .filename").size() > 0)
					jQuery("#UploadQueue #"+item.id+" .filename").html( item.filename );
				if(jQuery("#UploadQueue #"+item.id+" .progress").size() > 0)
					jQuery("#UploadQueue #"+item.id+" .progress").animate({width: item.percent+"%"}, 'slow');

				if(jQuery("#UploadQueue #"+item.id+" .total").size() > 0)
					jQuery("#UploadQueue #"+item.id+" .total").html( item.total );
				if(jQuery("#UploadQueue #"+item.id+" .uploaded").size() > 0)
					jQuery("#UploadQueue #"+item.id+" .uploaded").html( item.uploaded );
				if(jQuery("#UploadQueue #"+item.id+" .speed").size() > 0)
					jQuery("#UploadQueue #"+item.id+" .speed").html( item.speed );

				// remove li when done
				if( item.status == "Done" ){
					jQuery("#UploadQueue #"+item.id).delay(5000).fadeOut("slow", function(){
						jQuery(this).remove();
					});
				}
				else if( item.status == "Error" ){
					jQuery("#UploadQueue #"+item.id).delay(30000).fadeOut("slow", function(){
						jQuery(this).remove();
					});
				}
			});
		},
		error: function(request, textStatus){
			alert(textStatus);
		}
	});
}