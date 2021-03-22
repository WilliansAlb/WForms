package Analizador;
import java_cup.runtime.Symbol;
%%
%class LexerDB
%type java_cup.runtime.Symbol
%cup
%full
%line
%column
%char
%public
L=[a-zA-Z]+
D=[0-9]+
C=[@_"-"%#&]+
espacio=[ |\t|\r|\n]+
esp = [ ]+
%{
    private Symbol symbol(int type, Object value){
        return new Symbol(type, yyline, yycolumn, value);
    }
    private Symbol symbol(int type){
        return new Symbol(type, yyline, yycolumn);
    }
%}
%%

/* Inicio de mas de una solicitud */
( "ini_solicitudes" ) {return new Symbol(sym2.SOLICITUDESP, yycolumn, yyline, yytext());}

/* Fin de mas de una solicitud */
( "fin_solicitudes" ) {return new Symbol(sym2.FINSOLICITUDES, yycolumn, yyline, yytext());}

/* Inicio de etiqueta*/
( "<!" ) {return new Symbol(sym2.INICIOE,yycolumn,yyline,yytext());}

/* Fin de etiqueta*/
( "!>" ) {return new Symbol(sym2.FINE,yycolumn,yyline,yytext());}

/* Menor que */
( "<" ) {return new Symbol(sym2.MENORQ,yycolumn,yyline,yytext());}

/* Mayor que */
( ">" ) {return new Symbol(sym2.MAYORQ,yycolumn,yyline,yytext());}

/* O */
( "|" ) {return new Symbol(sym2.OR,yycolumn,yyline,yytext());}

/* Inicio de una solicitud */
( "ini_solicitud" ) {return new Symbol(sym2.INICIOS, yycolumn, yyline, yytext());}

/* Fin de una solicitud*/
( "fin_solicitud" ) {return new Symbol(sym2.FINS,yycolumn,yyline,yytext());}

/* Crear usuario */
CREAR_USUARIO {return new Symbol(sym2.CREARU,yycolumn,yyline,yytext());}

/* Usuario */
("USUARIO") {return new Symbol(sym2.USUARIOP,yycolumn,yyline,yytext());}

/* From */
("->") {return new Symbol(sym2.DE,yycolumn,yyline,yytext());}

/* Where */
("WHERE") {return new Symbol(sym2.WHERE,yycolumn,yyline,yytext());}

/* Signo igual */
("=") {return new Symbol(sym2.IGUAL,yycolumn,yyline,yytext());}

/* Valor de comparacion */
(">=") {return new Symbol(sym2.MAYORIGUAL,yycolumn,yyline,yytext());}

/* Valor de comparacion */
("<=") {return new Symbol(sym2.MENORIGUAL,yycolumn,yyline,yytext());}

/* Valor de comparacion */
("AND"|"OR") {return new Symbol(sym2.OPERADORLOGICO,yycolumn,yyline,yytext());}

/* Operador not */
("NOT") {return new Symbol(sym2.OPERADORNOT,yycolumn,yyline,yytext());}

/* Contraseña*/
("PASSWORD") {return new Symbol(sym2.CONTRAP,yycolumn,yyline,yytext());}

/*Fecha de creación*/
("FECHA_CREACION") {return new Symbol(sym2.FECHACP,yycolumn,yyline,yytext());}

/*Palabra reservada para especificar que el tipo de solicitud es una consulta*/
("CONSULTAR_DATOS") {return new Symbol(sym2.CONSULTADATOS,yycolumn,yyline,yytext());}

/*Consultas*/
("CONSULTAS") {return new Symbol(sym2.CONSULTAS,yycolumn,yyline,yytext());}

/*Modificar usuario*/
("MODIFICAR_USUARIO") {return new Symbol(sym2.MODIFICARU,yycolumn,yyline,yytext());}

/*Usuario antiguo*/
("USUARIO_ANTIGUO") {return new Symbol(sym2.USUARIOAP,yycolumn,yyline,yytext());}

/*Usuario nuevo*/
("USUARIO_NUEVO") {return new Symbol(sym2.USUARIONP,yycolumn,yyline,yytext());}

/*Contraseña nueva*/
("NUEVO_PASSWORD") {return new Symbol(sym2.NUEVOP,yycolumn,yyline,yytext());}

/*Fecha modificación*/
("FECHA_MODIFICACION") {return new Symbol(sym2.FECHAMP,yycolumn,yyline,yytext());}

/*Eliminar usuario*/
("ELIMINAR_USUARIO") {return new Symbol(sym2.ELIMINARU,yycolumn,yyline,yytext());}

/*Login usuario*/
("LOGIN_USUARIO") {return new Symbol(sym2.LOGINU,yycolumn,yyline,yytext());}

/*Palabra reservada para nuevo formulario*/
("NUEVO_FORMULARIO") {return new Symbol(sym2.NUEVOFP,yycolumn,yyline,yytext());}

/*Palabra reservada para ID*/
("ID") {return new Symbol(sym2.IDP,yycolumn,yyline,yytext());}

/*Palabra reservada para Titulo*/
("TITULO") {return new Symbol(sym2.TITULOP,yycolumn,yyline,yytext());}

/*Palabra reservada para nombre*/
("NOMBRE") {return new Symbol(sym2.NOMBREP,yycolumn,yyline,yytext());}

/*Palabra reservada para tema*/
("TEMA") {return new Symbol(sym2.TEMAP,yycolumn,yyline,yytext());}

/*Palabra reservada para creacion de usuario*/
("USUARIO_CREACION") {return new Symbol(sym2.USUARIOCP,yycolumn,yyline,yytext());}

/*Palabra reservada para tipos de temas*/
("dark"|"blue"|"white"|"DARK"|"BLUE"|"WHITE") {return new Symbol(sym2.TEMA,yycolumn,yyline,yytext());}

/*Palabra reservada para clases normales*/
("CAMPO_TEXTO"|"FICHERO") {return new Symbol(sym2.CLASENORMAL,yycolumn,yyline,yytext());}

/*Palabra reservada para clases area texto*/
("AREA_TEXTO") {return new Symbol(sym2.CLASEAREA,yycolumn,yyline,yytext());}

/*Palabra reservada para clases con opciones*/
("CHECKBOX"|"RADIO"|"COMBO") {return new Symbol(sym2.CLASEOPCIONES,yycolumn,yyline,yytext());}

/*Palabra reservada para clases de imagen*/
("IMAGEN") {return new Symbol(sym2.CLASEIMAGEN,yycolumn,yyline,yytext());}

/* Select */
("SELECT") {return new Symbol(sym2.SELECT,yycolumn,yyline,yytext());}

/* Select */
("TO") {return new Symbol(sym2.TO,yycolumn,yyline,yytext());}

/* Select */
("FORM") {return new Symbol(sym2.FORM,yycolumn,yyline,yytext());}

/*Palabra reservada para clase de boton*/
("BOTON") {return new Symbol(sym2.CLASEBOTON,yycolumn,yyline,yytext());}

/*Palabra reservada para tipos de temas*/
("SI"|"NO") {return new Symbol(sym2.BOOL,yycolumn,yyline,yytext());}

/*Palabra reservada para eliminar formulario*/
("ELIMINAR_FORMULARIO") {return new Symbol(sym2.ELIMINARF,yycolumn,yyline,yytext());}

/*Palabra reservada para modificar formulario*/
("MODIFICAR_FORMULARIO") {return new Symbol(sym2.MODIFICARF,yycolumn,yyline,yytext());}

/*Palabra reservada para parametros*/
("PARAMETROS_FORMULARIO") {return new Symbol(sym2.PARAMF,yycolumn,yyline,yytext());}

/*Palabra reservada para agregar componente*/
("AGREGAR_COMPONENTE") {return new Symbol(sym2.AGREGARC,yycolumn,yyline,yytext());}

/*Palabra reservada para nombre del campo*/
("NOMBRE_CAMPO") {return new Symbol(sym2.NOMBREC,yycolumn,yyline,yytext());}

/*Palabra reservada para formulario*/
("FORMULARIO") {return new Symbol(sym2.FORMULARIOC,yycolumn,yyline,yytext());}

/*Palabra reservada para clase*/
("CLASE") {return new Symbol(sym2.CLASE,yycolumn,yyline,yytext());}

/*Palabra reservada para indice*/
("INDICE") {return new Symbol(sym2.INDICEC,yycolumn,yyline,yytext());}

/*Palabra reservada para el texto visible del componente*/
("TEXTO_VISIBLE") {return new Symbol(sym2.TEXTOVC,yycolumn,yyline,yytext());}

/*Palabra reservada para alineacion*/
("ALINEACION") {return new Symbol(sym2.ALINEAC,yycolumn,yyline,yytext());}

/*Palabra reservada para requerido*/
("REQUERIDO") {return new Symbol(sym2.REQUERIDO,yycolumn,yyline,yytext());}

/*Palabra reservada para opciones*/
("OPCIONES") {return new Symbol(sym2.OPCIONES,yycolumn,yyline,yytext());}

/*Palabra reservada para filas*/
("FILAS") {return new Symbol(sym2.FILAS,yycolumn,yyline,yytext());}

/*Palabra reservada para columnas*/
("COLUMNAS") {return new Symbol(sym2.COLUMNAS,yycolumn,yyline,yytext());}

/*Palabra reservada para url*/
("URL") {return new Symbol(sym2.URLC,yycolumn,yyline,yytext());}

/*Palabra reservada para eliminar*/
("ELIMINAR_COMPONENTE") {return new Symbol(sym2.ELIMINARC,yycolumn,yyline,yytext());}

/*Palabra reservada para parametros de los componentes*/
("PARAMETROS_COMPONENTE") {return new Symbol(sym2.PARAMC,yycolumn,yyline,yytext());}

/*Palabra reservada para modificar los componentes*/
("MODIFICAR_COMPONENTE") {return new Symbol(sym2.MODIFICARC,yycolumn,yyline,yytext());}

/*Palabra reservada para mandar solicitud para las credenciales del usuario*/
("CREDENCIALES_USUARIO") {return new Symbol(sym2.CREDENCIALES,yycolumn,yyline,yytext());}

/* Dos puntos */
( ":" ) {return new Symbol(sym2.DOSP, yycolumn, yyline, yytext());}

/* Coma */
( "," ) {return new Symbol(sym2.COMA, yycolumn, yyline, yytext());}

/* Parentesis de apertura */
( "(" ) {return new Symbol(sym2.PARENTESISA, yycolumn, yyline, yytext());}

/* Parentesis de cierre */
( ")" ) {return new Symbol(sym2.PARENTESISC, yycolumn, yyline, yytext());}

/* Llave de apertura */
( "{" ) {return new Symbol(sym2.LLAVEA, yycolumn, yyline, yytext());}

/* Llave de cierre */
( "}" ) {return new Symbol(sym2.LLAVEC, yycolumn, yyline, yytext());}

/* Apostrofe */
( "'" ) {return new Symbol(sym2.APOSTROFE, yycolumn, yyline, yytext());}

/* Corchete de apertura */
( "[" ) {return new Symbol(sym2.CORCHETEA, yycolumn, yyline, yytext());}

/* Corchete de cierre */
( "]" ) {return new Symbol(sym2.CORCHETEC, yycolumn, yyline, yytext());}

/* Comillas */
( "\"" ) {return new Symbol(sym2.COMILLAS, yycolumn, yyline, yytext());}

/* Numero de consulta */
("CONSULTA-"{D}+) {return new Symbol(sym2.NOCONSULTA,yycolumn,yyline,yytext());}

/* Numero */
{D}+|{D}+"."{D}+ {return new Symbol(sym2.NUMERO, yycolumn, yyline, yytext());}

/*Fecha*/
{D}{4}"-"({D}{2}"-"|{D}"-")({D}{2}|{D}) {return new Symbol(sym2.FECHA,yycolumn,yyline,yytext());}

/*Usuario token*/
({L}|{D}|{C})+ {return new Symbol(sym2.USUARIO,yycolumn,yyline,yytext());}

/* ID */
("$"|"_"|"-")("$"|"_"|"-"|{D}|{L})* {return new Symbol(sym2.ID,yycolumn,yyline,yytext());}

/*URL*/
("https://")?{L}({L})*"."{L}({L})*".com/"(({L}|{D}|{C})*("/")?)* {return new Symbol(sym2.URL, yycolumn,yyline,yytext());}

/*TITULO*/
("'")({L}|{D})({L}|{D}|{C}|{esp})*("'") {return new Symbol(sym2.TITULO, yycolumn, yyline, yytext());}

/* Espacios en blanco */
{espacio} {/*Ignore*/}

/* Comentarios */
( "//"(.)* ) {/*Ignore*/}

