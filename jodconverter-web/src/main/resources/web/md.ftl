<!DOCTYPE html>
<html lang="zh">
<head>
    <meta charset="utf-8" />
    <title>MD文件在线预览---${fileName}</title>
    <link rel="stylesheet" href="mdeditor/css/style.css" />
    <link rel="stylesheet" href="mdeditor/css/editormd.css" />
    <link rel="shortcut icon" href="https://pandao.github.io/editor.md/favicon.ico" type="image/x-icon" />
</head>
<body>
<div id="layout">
    <header>
        <h1>${fileName}</h1>
    </header>
    <div id="test-editormd">
                <textarea style="display:none;">${content}</textarea>
    </div>
</div>
<script src="mdeditor/js/jquery.min.js"></script>
<script src="mdeditor/js/editormd.min.js"></script>
<script type="text/javascript">
    var testEditor;

    $(function() {
        testEditor = editormd("test-editormd", {
            width   : "90%",
            height  : 640,
            syncScrolling : "single",
            path    : "mdeditor/lib/"
        });

    });
</script>
</body>
</html>