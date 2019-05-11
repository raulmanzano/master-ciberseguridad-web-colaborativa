package controllers;


import helpers.HashUtils;
import models.User;
import play.i18n.Messages;
import play.mvc.Controller;

public class Secure extends Controller {

    public static void login(){
        render();
    }

    public static void logout(){
        session.remove("password");
        login();
    }

    public static void authenticate(String username, String password){
    	// se incorpora un contador de intentos para deshabilitar los usuarios que hagan muchos intentos en una misma sesion.
        User u = User.loadUser(username);
        
        if (session.get("intentos")==null)
        	{session.put("intentos","0");
        	System.out.println("Estableciendo valor inicial a "+ session.get("intentos"));}
        
        if (u != null && u.getPassword().equals(HashUtils.getMd5(password))){
        	//session.put("intentos",null);
        	//System.out.println(session.get("intentos"));
        	session.put("username", username);
            session.put("password", password);
            Application.index();
        }else{
            if ((session.get("intentos")!=null) && (Integer.valueOf(session.get("intentos")) <= 3))
            {
            	flash.put("error", Messages.get("Public.login.error.credentials"));
            	session.put("intentos",String.valueOf(Integer.valueOf(session.get("intentos"))+1));
            	System.out.println("Incrementando por errores a "+ session.get("intentos"));
            	login();	
            }
            else
            {	//usuario deshabilitado
            	u.setPassword(null);
            	flash.put("error", Messages.get("Public.login.error.disabled"));
            	System.out.println("Excedido en "+ session.get("intentos"));
            	login();
            }
        }

    }
}
