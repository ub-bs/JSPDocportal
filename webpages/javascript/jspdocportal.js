  
  function reallyDeletefromDB() {
	return confirm("Achtung! \n Das gesamte Dokument wird damit aus der Digitalen Bibliothek gel�scht!");
  }
  
  function checkMyForm()	{
    var prefix="/mycoreobject/metadata/"
    var elAnz = document.forms[0].length;	
    var ret=true;
    for (var i = 0; i < elAnz; ++i){
    	var myElem = document.forms[0].elements[i];
    	var type = myElem.getAttribute("class");
    	if ( type=="mandatory" ){    
    	    if ( myElem.value==null || myElem.value=="" ) {
				document.forms[0].elements[i].setAttribute("style", "background-color: rgb(255,240,240); background: rgb(255,240,240); ");	
				ret=false;
			} else {
			 //reset if fails in former time
	  		 document.forms[0].elements[i].setAttribute("style", "background-color: rgb(255,255,255); background: rgb(255,255,255); ");	    	
	  		}
    	}
    	
    }
    if ( !ret )	alert("Es fehlen einige Pflichtangaben. \n Diese markierten Felder bitte ausfuellen." );      
	return ret;
  }
  
  function checkMyFormPerson()	{
    var ret=true;
    ret = checkMyForm();
    return ret;
  }

  function checkMyFormDocument()	{
    var ret=true;
    ret = checkMyForm();
    return ret;
  }

  
  function fenster(source,title) {
	var win1=null;
	var attr;
	attr="width=450,height=700,scrollbars=yes,resizable=yes,menubar=no,left=100,top=10";
    win1=window.open(source,"Ansicht",attr);
    win1.focus();
	return false;
  }

  function checkMyFormArticle()	{
    var ret=true;
    ret = checkMyForm();
    return ret;
  }
  function checkMyFormDisshab()	{
    var ret=true;
    ret = checkMyForm();
    return ret;
  }
  function checkMyFormPortrait()	{
    var ret=true;
    ret = checkMyForm();
    return ret;
  }

  function checkMyFormProfessorum()	{
    var ret=true;
    ret = checkMyForm();
    return ret;
  }

 
 
