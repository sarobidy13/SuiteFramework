
import etu1816.framework.FileUpload;
import etu1816.framework.ModelView;
import etu1816.framework.annotation.*;

public class Test {

    private int id;
    private String nom;

    @MethodAnnotation(url = "save.do")
    public ModelView save() {
        ModelView mv = new ModelView();

        mv.setView("/test.jsp");
        mv.addItem("obj", this);

        return mv;
    }

    @MethodAnnotation(url = "setNewTest.do", paramName = "identifiant,name")
    public ModelView newTest(int identifiant, String name) {
        ModelView mv = new ModelView();

        this.setId(identifiant);
        this.setNom(name);

        mv.setView("/test.jsp");
        mv.addItem("obj", this);

        return mv;
    }
    @MethodAnnotation( url = "login.do", paramName="profil")
    public ModelView test(String profil) {
        ModelView mv = new ModelView();

        mv.setView("/index.jsp");
        mv.addSession("userProfil", profil);

        return mv;
    }

    @Authentification( profil = "admin")
    @MethodAnnotation( url = "save.do")
    public ModelView save() {
        ModelView mv = new ModelView();

        mv.setView("/test.jsp");
        mv.addItem("obj", this);

        return mv;
    }
    @Authentification( profil = "admin,simple")
    @MethodAnnotation( url = "setNewTest.do", paramName = "identifiant,name")
    public ModelView newTest(int identifiant, String name) {
        ModelView mv = new ModelView();

        this.setId(identifiant);
        this.setNom(name); 

        mv.setView("/test.jsp");
        mv.addItem("obj", this);

        return mv;
    }

    @MethodAnnotation(url = "upload.do", paramName = "fu")
    public ModelView upload(FileUpload fu) {
        ModelView mv = new ModelView();

        this.setId(0);
        this.setNom("Sarobidy");

        mv.setView("/test.jsp");
        mv.addItem("obj", this);

        return mv;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNom() {
        return this.nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }
}