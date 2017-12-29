var app={

}

var image_count;
var parentPath="";

$(document).ready(function(){

    loadImage();


    $(window).resize(function() {
        console.log("resize");
        //loadImage();
    });

    $(window).scroll(function() {

    });

    $("#all_photo").click(function(){
        loadImage();
    });

    $("#folder").click(function(){
        loadFolders();
    });

    //文件管理
    $("#folder-manager").click(function(){
        loadFiles(parentPath);
    });

    //剪贴板
    $("#clipboard-manager").click(function(){
        loadClip();
        setInterval(function(){
           loadClip();
        },10000);
    })
});

function loadClip(){
    $.ajax({
        url:"http://172.27.2.160:8081/clip/get",
        dataType:"json",
        success:function(result){
            if(result.status ==0){
                if(result.data&&result.data.mItems){
                    result.data.mItems.forEach(function(val,index,arr){
                        $(".right-side-content").html(val.mText);
                    });
                }
            }
        }
    });
}

function loadFiles(parent){
    parentPath += parent;

    if(parentPath.length > 0)
        parentPath += "/";
    $(".right-side-content").html("");
    $.ajax({
        url:"http://172.27.2.160:8081/file/list",
        data:{
            parent:parent
        },
        dataType:"json",
        success:function(result){
            if(result.status==0){
                result.list.forEach(function(val,index,arr){
                    var iStr = "";
                    if(val.isDir){
                        iStr = "<i class='fa fa-folder-o' aria-hidden='true' onclick='loadFiles(\""+parentPath + val.name+"\")' style='cursor:pointer;font-size: 5em;'></i>";

                    }else{
                        iStr = "<i class='fa fa-file' aria-hidden='true' onclick='' style='cursor:pointer;font-size: 5em;'></i>";
                    }
                    $(".right-side-content").append("<div class='folder' style='text-align:center;'>"+iStr+"<div style='text-align:center;'>"+val.name+"</div></div> ")
                });
            }
        }
    });
}

function loadFullImage(id){
    var width = ($(document).width() < $('body').width() ? $(document).width() : $('body').width());
    var height = $(document).height() < $('body').height() ? $(document).height() : $('body').height();

    //页面层-自定义
    layer.open({
      type: 1,
      title: false,
      area: [width * 0.8,height * 0.8],
      closeBtn: 0,
      shadeClose: true,
      skin: 'layer-css',
      content: '<img src="http://172.27.2.160:8081/image/full?id='+id+'" style="max-width:100%;max-height:100%;"/>'
    });

}

function imgLoad(){
    layer.load();
}

function loadImage(){
    var right_content_margin = 60;//菜单宽度
    var image_size = 90;//图片大小
    var width = ($(document).width() < $('body').width() ? $(document).width() : $('body').width())-right_content_margin;
    var height = $(document).height() < $('body').height() ? $(document).height() : $('body').height();

    $(".right-side-content").css("height",height)
    $(".right-side-content").html("");

    console.log(width + "  "+height)

    var rows = parseInt(height / image_size);
    var cols = parseInt(width / image_size);

    var image_count = rows * cols;

    console.log(rows +"  " +cols+"  " + image_count);

    loadImageData();
}

function loadImageData(){
    $.ajax({
        url:'http://172.27.2.160:8081/image/list',
        dataType:'json',
        success:function(result){
            if(result.status==0){
                result.list.forEach(function(val,index,arr){
                    $(".right-side-content").append("<img src='http://172.27.2.160:8081/image/thumbnail?id="+val+"' onclick='loadFullImage("+val+")' style='cursor:pointer;'/>")
                });
            }
        },
        error:function(){

        }
    });
}

function loadImageDataByBucket(bucket){
    $(".right-side-content").html("");

    $.ajax({
            url:'http://172.27.2.160:8081/image/list',
            data:{
                bucket:bucket,
                offset:0,
                size:100
            },
            dataType:'json',
            success:function(result){
                if(result.status==0){
                    result.list.forEach(function(val,index,arr){
                        $(".right-side-content").append("<img src='http://172.27.2.160:8081/image/thumbnail?id="+val+"' onclick='loadFullImage("+val+")' style='cursor:pointer;'/>")
                    });
                }
            },
            error:function(){

            }
        });
}

function loadFolders(){

    $(".right-side-content").html("");

    $.ajax({
        url:"http://172.27.2.160:8081/image/folder",
        dataType:"json",
        success:function(result){
            if(result.status == 0){
                result.list.forEach(function(val,index,arr){
                    $(".right-side-content").append("<div class='folder'><div class='card1'></div><div class='card2'></div><img src='http://172.27.2.160:8081/image/thumbnail?id="+val.cover+"' onclick='loadImageDataByBucket(\""+val.name+"\")' /><div class='name'>"+val.name+"</div><div id='circle'>"+val.count+"</div></div>")
                });
            }
        },
        error:function(){}
    });
}