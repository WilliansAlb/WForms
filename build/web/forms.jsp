<%-- 
    Document   : forms
    Created on : 25/03/2021, 18:12:17
    Author     : willi
--%>

<%@page import="Controladores.ControladorFormulario"%>
<%@page import="POJOS.Formulario"%>
<%@page import="java.util.ArrayList"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Forms</title>
        <link rel="stylesheet" href="style.css" />
        <link rel="stylesheet" href="style2.css" />
        <link rel="shortcut icon" type="image/x-icon" href="img/forms.svg" />
    </head>
    <body>
        
        <%
            HttpSession actual = request.getSession();
            if (actual.getAttribute("USUARIO") != null) {
        %>
        <div id="blurry-filter"></div>
        <header>
            <div>
                <article id="title"><span class="parent">tus formularios</span><br /><span class="name">WF<a style="display:contents;text-decoration: none;color:#84d404;"><img src="img/forms.svg" style="width: 25px;height: 25px;"></a>RMS</span>
                </article>
                <article id="reference">
                    <a style="display:contents;text-decoration: none;color:#84d404;"><img src="img/user.svg" style="width: 25px;height: 25px;"><span style="font-size:18px;"><%out.print(actual.getAttribute("USUARIO").toString());%></span></a><div class="cerrar"><a href="http://localhost:8080/WForms/Login?logout=si" style="display:contents;text-decoration: none;"><img src="img/salir.svg" style="width: 25px;height: 25px;"><span style="font-size:12px;">CERRAR SESION</span></a></div></article>
            </div>
        </header>
        <%
            ControladorFormulario control = new ControladorFormulario();
            ArrayList<Formulario> forms = control.tusFormularios(actual.getAttribute("USUARIO").toString());
            if (forms.size() > 0) {

        %>
        <div class="paraFlex">
            <div class="contenedor">
                <%            for (int i = 0; i < forms.size(); i++) {
                        Formulario fm = forms.get(i);
                %>
                <div class="flip-card-container" style="--hue: 220">
                    <div class="flip-card">
                        <div class="card-front">
                            <figure>
                                <div class="img-bg"></div>
                                <img
                                    src="https://cdn.rsjoomla.com/images/products/header-image-joomla-extension-rsform.png">
                                <figcaption><%out.print(fm.getId());%></figcaption>
                            </figure>
                            <div class="datos">
                                <p><span class="nombre_param">TITULO:</span><span class="param"><%out.print(fm.getTitulo());%></span></p>
                                <p><span class="nombre_param">NOMBRE:</span><span class="param"><%out.print(fm.getNombre());%></span></p>
                                <p><span class="nombre_param">TEMA:</span><span class="param"><%out.print(fm.getTema());%></span></p>
                                <p><span class="nombre_param">ID:</span><span class="param"><%out.print(fm.getId());%></span></p>
                                <p><span class="nombre_param">FECHA CREACION:</span><span class="param"><%out.print(fm.getFecha());%></span></p>
                            </div>
                        </div>
                        <div class="card-back">
                            <figure>
                                <div class="img-bg"></div>
                                <img
                                    src="https://cdn.rsjoomla.com/images/products/header-image-joomla-extension-rsform.png">
                            </figure>
                            <button onclick="window.location = 'Ver?id=<%out.print(fm.getId());%>';">Ver</button>
                            <div class="design-container">
                                <span class="design design--1"></span>
                                <span class="design design--2"></span>
                                <span class="design design--3"></span>
                                <span class="design design--4"></span>
                                <span class="design design--5"></span>
                                <span class="design design--6"></span>
                                <span class="design design--7"></span>
                                <span class="design design--8"></span>
                            </div>
                        </div>
                    </div>
                </div>
                <%}%>
            </div>
        </div>
        <%} else {%>
        <div class="paraFlex">
            <img src="img/folder.svg" width="30%">
            <h1 style="position: absolute;backdrop-filter: blur(20px);z-index: 20;color:rgb(207, 243, 141);">Aún no has creado ningún formulario</h1>
        </div>

        <%}%>
        <%} else {
                response.sendRedirect("http://localhost:8080/WForms/");
            }%>
    </body>
</html>
