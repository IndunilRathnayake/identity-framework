<!doctype html>

<title>CodeMirror: XML Autocomplete Demo</title>
<meta charset="utf-8"/>
<link rel=stylesheet href="codemirror/doc/docs.css">

<link rel="stylesheet" href="codemirror/lib/codemirror.css">
<link rel="stylesheet" href="codemirror/addon/hint/show-hint.css">
<script src="codemirror/lib/codemirror.js"></script>
<script src="codemirror/addon/hint/show-hint.js"></script>
<script src="codemirror/addon/hint/xml-hint.js"></script>
<script src="codemirror/mode/xml/xml.js"></script>
<style type="text/css">
    .CodeMirror { border: 1px solid #eee; }
</style>
<div id=nav>
    <a href="https://codemirror.net"><h1>CodeMirror</h1><img id=logo src="../doc/logo.png"></a>

    <ul>
        <li><a href="../index.html">Home</a>
        <li><a href="../doc/manual.html">Manual</a>
        <li><a href="https://github.com/codemirror/codemirror">Code</a>
    </ul>
    <ul>
        <li><a class=active href="#">XML Autocomplete</a>
    </ul>
</div>

<article>
    <h2>XML Autocomplete Demo</h2>
    <form><textarea id="code" name="code"><!-- write some xml below -->
</textarea></form>

    <p>Press <strong>ctrl-space</strong>, or type a '&lt;' character to
        activate autocompletion. This demo defines a simple schema that
        guides completion. The schema can be customized—see
        the <a href="../doc/manual.html#addon_xml-hint">manual</a>.</p>

    <p>Development of the <code>xml-hint</code> addon was kindly
        sponsored
        by <a href="http://www.xperiment.mobi">www.xperiment.mobi</a>.</p>

    <script>
        var dummy = {
            attrs: {
                color: ["red", "green", "blue", "purple", "white", "black", "yellow"],
                size: ["large", "medium", "small"],
                description: null
            },
            children: []
        };

        var tags = {
            "!top": ["top"],
            "!attrs": {
                id: null,
                class: ["A", "B", "C"]
            },
            top: {
                attrs: {
                    lang: ["en", "de", "fr", "nl"],
                    freeform: null
                },
                children: ["animal", "plant"]
            },
            animal: {
                attrs: {
                    name: null,
                    isduck: ["yes", "no"]
                },
                children: ["wings", "feet", "body", "head", "tail"]
            },
            plant: {
                attrs: {name: null},
                children: ["leaves", "stem", "flowers"]
            },
            wings: dummy, feet: dummy, body: dummy, head: dummy, tail: dummy,
            leaves: dummy, stem: dummy, flowers: dummy
        };

        function completeAfter(cm, pred) {
            var cur = cm.getCursor();
            if (!pred || pred()) setTimeout(function() {
                if (!cm.state.completionActive)
                    cm.showHint({completeSingle: false});
            }, 100);
            return CodeMirror.Pass;
        }

        function completeIfAfterLt(cm) {
            return completeAfter(cm, function() {
                var cur = cm.getCursor();
                return cm.getRange(CodeMirror.Pos(cur.line, cur.ch - 1), cur) == "<";
            });
        }

        function completeIfInTag(cm) {
            return completeAfter(cm, function() {
                var tok = cm.getTokenAt(cm.getCursor());
                if (tok.type == "string" && (!/['"]/.test(tok.string.charAt(tok.string.length - 1)) || tok.string.length == 1)) return false;
                var inner = CodeMirror.innerMode(cm.getMode(), tok.state).state;
                return inner.tagName;
            });
        }

        var editor = CodeMirror.fromTextArea(document.getElementById("code"), {
            mode: "xml",
            lineNumbers: true,
            extraKeys: {
                "'<'": completeAfter,
                "'/'": completeIfAfterLt,
                "' '": completeIfInTag,
                "'='": completeIfInTag,
                "Ctrl-Space": "autocomplete"
            },
            hintOptions: {schemaInfo: tags}
        });
    </script>
</article>
