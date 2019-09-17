function show() {
    document.getElementById("sidebar").style.width = "300px";
    document.getElementById("open").style.display = "none";
    document.getElementById("close").style.display = "inline";
}

function hide() {
    document.getElementById("sidebar").style.width = "0";
    document.getElementById("open").style.display = "inline";
    document.getElementById("close").style.display = "none";
}

/*This is for prevent crash of html with out of range numbers*/
document.getElementById("link-strength").oninput = function(){
    if (document.getElementById("link-strength").value > 5 || document.getElementById("link-strength").value < 0) {
        document.getElementById('redraw').hidden = true;
        document.getElementById('link-strength').style.border = "3px solid red";
    } else {
        document.getElementById('redraw').hidden = false;
        document.getElementById('link-strength').style.border = null;
    }
}



