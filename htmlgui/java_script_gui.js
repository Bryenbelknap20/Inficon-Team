function createFile(decision) {
    const newId = document.getElementById("result").childElementCount;
    const reader = new FileReader();
    var file;

    if(decision == null) {
        file = document.getElementById("oneFile").files[0];
    }
    else {
        file = document.getElementById("filesInBulk").files[decision];
    }
    if(file){
        reader.readAsDataURL(file);
        reader.onload = function(){
            //document.getElementById("testPrintNumOne").innerHTML = "Base64 File Format: " + reader.result;
            reader.result;
        }
        reader.onerror = function(error){
            document.getElementById("testPrintNumOne").innerHTML = "Base64 File Format: error";
        }

        if(typeof(Storage) !== "undefined") {
            sessionStorage.setItem("originalFile." + newId, reader.result);
        }
        else {
            document.getElementById("result").innerHTML = "No web storage support.";
        }


        var x = document.createElement("DIV");
        x.setAttribute("id", newId);
        x.textContent = "File: " + file.name;

        var y = document.createElement("INPUT");
        y.setAttribute("type", "submit");
        y.setAttribute("class", "interactable");
        y.setAttribute("id", "input 1: " + newId);


        var z = document.createElement("INPUT");
        z.setAttribute("type", "submit");
        z.setAttribute("value", "remove");
        z.setAttribute("class", "interactable");
        z.setAttribute("onClick", "deleteSpecific(" + newId + ")");
        z.setAttribute("id", "input 2: " + newId);


        document.getElementById("result").appendChild(x);
        y.setAttribute("value", "New ID: " + newId);
        document.getElementById(newId).appendChild(y);
        document.getElementById(newId).appendChild(z);

        document.getElementById("testPrintNumOne").innerHTML = "Number of inputs: " + (newId+1);
    }
}

function createFiles() {
    var storedFiles = document.getElementById("filesInBulk");
    var initialIndex = document.getElementById("result").childElementCount;
    const dataTransfer = new DataTransfer();
    var finalIndex = 0;
    var test = "";

    for(let i = 0; i < storedFiles.files.length; i++) {
        test = test + ", " + storedFiles.files[i].name;
        createFile(i);
        document.getElementById("testPrintNumTwo").innerHTML = "Files: " + (initialIndex + (i * 3));
    }

    finalIndex = document.getElementById("result").childElementCount;

    document.getElementById("testPrintNumTwo").innerHTML = "Files: " + test + ". " + initialIndex + " => " + finalIndex;
}

function deleteLast() {
    const y = document.getElementById("result").lastElementChild;
    const x = document.getElementById("result").childElementCount;
    y.remove();
    sessionStorage.removeItem("originalFile." + (x-1));
    document.getElementById("testPrintNumOne").innerHTML = "Number of inputs: " + x;
}

function deleteSpecific(theDelChild) {
    const y = document.getElementById("result");

    y.removeChild(document.getElementById(theDelChild));
    sessionStorage.removeItem("originalFile." + theDelChild);

    var x = document.getElementById("result").childElementCount;

    for(let j = theDelChild; j < x; j++) {
        var target1 = document.getElementById(j + 1);
        var target2 = document.getElementById("input 1: " + (j + 1));
        var target3 = document.getElementById("input 2: " + (j + 1));
        var item = sessionStorage.getItem("originalFile." + (j + 1));

        sessionStorage.removeItem("originalFile." + (j+1));

        target2.setAttribute("value", "New ID: " + j);
        target3.setAttribute("onClick", "deleteSpecific(" + j + ")");

        target1.setAttribute("id", j);
        target2.setAttribute("id", "input 1: " + j);
        target3.setAttribute("id", "input 2: " +  j);

        sessionStorage.setItem("originalFile." + j, item);
    }

    document.getElementById("testPrintNumOne").innerHTML = "Number of inputs: " + document.getElementById("result").childElementCount;
}