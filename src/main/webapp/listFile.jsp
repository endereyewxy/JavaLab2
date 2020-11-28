<%@ page import="cn.cyyself.WebApp.service.FileIO" %>
<%@ page import="util.Pair" %>
<%@ page import="java.io.File" %>
<%@ page import="java.net.URLEncoder" %>
<%@ page import="java.beans.Encoder" %>
<%@ page import="org.apache.commons.text.StringEscapeUtils" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    String path = FileIO.getPath((String) request.getAttribute("path"));
%>
<html>
<head>
    <title>文件浏览</title>
    <meta charset="utf-8">
    <link rel="stylesheet" href="/resource/style/style.css">
    <script src="/resource/js/jquery.min.js"></script>
</head>
<body class="index" id="root">
    <input type="file" id="fileUpload" hidden />
    <div id="topbar">
        <div id="toolbar">
            <div id="upload" class="tool">
                <img src="/resource/img/upload.svg" alt="upload">
            </div>
            <div id="new_folder" class="tool">
                <img src="/resource/img/folder-page.svg" alt="new_folder">
            </div>
        </div>
        <div id="flowbar">
            <div id="crumbbar">
                <%
                    String splited_path[] = ("files"+path).split("/");
                    String cur_path = "/";
                %>
                <%
                    for (int i=0;i<splited_path.length;i++) {
                        cur_path += URLEncoder.encode(splited_path[i],"UTF-8") + "/";
                %>
                <a class="crumb<%=i==splited_path.length-1?" active":"" %>" href="<%=cur_path%>">
                    <img class="sep" src="/resource/img/crumb.svg" alt=">">
                    <span class="label"><%=StringEscapeUtils.escapeHtml4(splited_path[i])%></span>
                </a>
                <%
                    }
                %>
            </div>
        </div>
    </div>
    <div id="mainrow">
        <div id="content">
            <div id="content-header" class="hidden"></div>
            <div id="view" class="view-details view-size-20 width-1">
                <ul id="items" class="clearfix">
                    <li class="header">
                        <a class="icon"></a>
                        <a class="label">
                            <span>文件名</span>
                        </a>
                    </li>
                    <%

                        if (splited_path.length >= 2) {
                    %>
                    <li class="item folder folder-parent">
                        <a href="../">
                            <span class="icon square"><img src="/resource/img/folder-parent.svg" alt="folder"></span>
                            <span class="icon landscape"><img src="/resource/img/folder-parent.svg" alt="folder"></span>
                            <span class="label" title="..">..</span>
                        </a>
                    </li>
                    <%
                        }
                    %>
                    <%
                        for (File x : FileIO.listFile(path)) {
                            String type = x.isDirectory()?"folder":"file";
                    %>
                    <li class="item <%=type%>">
                        <a href="<%=URLEncoder.encode(x.getName(),"UTF-8")+(x.isDirectory()?"/":"")%>">
                            <span class="icon square"><img src="/resource/img/<%=type%>.svg" alt="folder"></span>
                            <span class="icon landscape"><img src="/resource/img/<%=type%>.svg" alt="folder"></span>
                            <span class="label" title="<%=StringEscapeUtils.escapeHtml4(x.getName())%>"><%=StringEscapeUtils.escapeHtml4(x.getName())%></span>
                        </a>
                    </li>
                    <%
                        }
                    %>
                </ul>
            </div>
        </div>
    </div>
<script>
    $("#new_folder").click(function () {
        var dir_name = prompt("请输入文件夹名：");
        $.ajax({
            type: "POST",
            url: ".",
            data: {
                'action': "mkdir",
                'dir_name': dir_name
            },
            success : function(result) {
                alert("创建成功");
            },
            dataType: 'json',
            error : function(e){
                alert("网络异常");
            }
        });
    });
</script>
</body>
</html>
