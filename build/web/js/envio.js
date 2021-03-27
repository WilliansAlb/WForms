/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
function enviarDatos(datos, id) {
    var pluginArrayArg = new Array();
    if (document.forms.length > 0) {
        for (var i = 0; i < document.forms[0].elements.length; i++) {
            var campo = document.forms[0].elements[i];
            if (campo.id !== '' && campo.value !== '') {
                var jsonArg1 = new Object();
                jsonArg1.id = campo.id;
                jsonArg1.nombre = campo.name;
                jsonArg1.registro = campo.value;
                jsonArg1.form = id;
                pluginArrayArg.push(jsonArg1);
            }
        }
    }
    var jsonArray = JSON.stringify(pluginArrayArg);
    console.log(jsonArray);
    $.ajax({
        type: "POST",
        url: "Ingresar",
        data: {seria: jsonArray, id: id},
        beforeSend: function () {
        },
        complete: function (data) {
        },
        success: function (data) {
            if (data.includes("ERROR"))
            {
                alert("No se pudieron ingresar correctamente los datos");
            } else {
                alert("Se ingresaron correctamente los datos");
                for (var i = 0; i < document.forms[0].elements.length; i++) {
                    var campo = document.forms[0].elements[i];
                    if (campo.id !== '' && campo.value !== '') {
                        campo.value = "";
                    }
                }
            }
        },
        error: function (data) {
            alert("Problemas al tratar de enviar el formulario");
        }
    });
}


