var app={

}

var image_count;

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
});

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
      content: '<img src="/image/full?id='+id+'" style="max-width:100%;max-height:100%;"/>'
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
        url:'/image/list',
        dataType:'json',
        success:function(result){
            if(result.status==0){
                result.list.forEach(function(val,index,arr){
                    $(".right-side-content").append("<img src='/image/thumbnail?id="+val+"' onclick='loadFullImage("+val+")' style='cursor:pointer;'/>")
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
        url:"/image/folder",
        dataType:"json",
        success:function(result){
            if(result.status == 0){
                result.list.forEach(function(val,index,arr){
                    $(".right-side-content").append("<div class='folder'><div class='card1'></div><div class='card2'></div><img src='/image/thumbnail?id="+val.cover+"' onclick=''/><div class='name'>"+val.name+"</div></div>")
                });
            }
        },
        error:function(){}
    });
}