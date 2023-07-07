
import etu1816.framework.ModelView;
import etu1816.framework.MethodAnnotation;

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