/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
function enviarDatos(datos, id, nombre) {
    var pluginArrayArg = new Array();
    if (document.forms.length > 0) {
        for (var i = 0; i < document.forms[0].elements.length; i++) {
            var campo = document.forms[0].elements[i];
            if (campo.id !== '' && campo.value !== '') {
                if (campo.type === 'textarea' || campo.type === 'text') {
                    var jsonArg1 = new Object();
                    jsonArg1.id = campo.id;
                    jsonArg1.nombre = campo.name;
                    jsonArg1.registro = campo.value;
                    jsonArg1.form = id;
                    jsonArg1.nombref = nombre;
                    pluginArrayArg.push(jsonArg1);
                } else if ((campo.type === 'checkbox' || campo.type === 'radio') && campo.checked) {
                    var jsonArg1 = new Object();
                    jsonArg1.id = campo.id;
                    jsonArg1.nombre = campo.name;
                    jsonArg1.registro = campo.value;
                    jsonArg1.form = id;
                    jsonArg1.nombref = nombre;
                    pluginArrayArg.push(jsonArg1);
                } else if (campo.type === 'select-one') {
                    var jsonArg1 = new Object();
                    jsonArg1.id = campo.id;
                    jsonArg1.nombre = campo.name;
                    jsonArg1.registro = campo.value;
                    jsonArg1.form = id;
                    jsonArg1.nombref = nombre;
                    pluginArrayArg.push(jsonArg1);
                }
            }
        }
    }
    var jsonArray = JSON.stringify(pluginArrayArg);
    $.ajax({
        type: "POST",
        url: "Ingresar",
        data: {seria: jsonArray, id: id, nombre: nombre},
        beforeSend: function () {
        },
        complete: function (data) {
        },
        success: function (data) {
            if (data.includes("ERROR"))
            {
                alert("No se pudieron ingresar correctamente los datos");
            } else {
                for (var i = 0; i < document.forms[0].elements.length; i++) {
                    var campo = document.forms[0].elements[i];
                    if (campo.id !== '' && campo.value !== '') {
                        if (campo.type === 'textarea' || campo.type === 'text') {
                            campo.value = "";
                        } else if ((campo.type === 'checkbox' || campo.type === 'radio') && campo.checked) {
                            campo.checked = false;
                        }
                    }
                }
                mostrarLink(data);
            }
        },
        error: function (data) {
            alert("Problemas al tratar de enviar el formulario");
        }
    });
}
function mostrarLink(link) {
    document.getElementById("oculto").style.display = "block";
    document.getElementById("link").value = link;
}
function ocultar(enviado) {
    enviado.style.display = "none";
}


