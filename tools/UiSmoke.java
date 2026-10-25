import dev.amaan.employees.EmployeeApp;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.nio.file.*;
import javax.imageio.ImageIO;
import java.util.*;
import java.util.List;
import java.util.concurrent.Callable;
/** Optional desktop smoke test, using fictional data and the app's actual buttons/dialogs. */
public class UiSmoke {
    static <T> T edt(Callable<T> c) throws Exception {
        var result=new java.util.concurrent.FutureTask<T>(c);SwingUtilities.invokeAndWait(result);return result.get();
    }
    static List<Component> children(Container root) {
        List<Component> all=new ArrayList<>();for(var c:root.getComponents()){all.add(c);if(c instanceof Container sub)all.addAll(children(sub));}return all;
    }
    static void click(Container root,String text) {var b=children(root).stream().filter(c->c instanceof JButton j && j.getText().equals(text)).map(c->(JButton)c).findFirst().orElseThrow();if(!b.isEnabled())throw new AssertionError("Disabled: "+text);b.doClick();}
    static JDialog dialog(String title) throws Exception {
        for(int i=0;i<100;i++){var d=edt(()->Arrays.stream(Window.getWindows()).filter(w->w instanceof JDialog j && j.isVisible() && j.getTitle().equals(title)).map(w->(JDialog)w).findFirst().orElse(null));if(d!=null)return d;Thread.sleep(50);}throw new AssertionError("No dialog: "+title);
    }
    static void ready(JFrame frame) throws Exception {
        for(int i=0;i<150;i++){if(edt(()->children(frame).stream().anyMatch(c->c instanceof JButton b && b.getText().equals("Add") && b.isEnabled())))return;Thread.sleep(50);}throw new AssertionError("UI busy");
    }
    static void open(JFrame f,String text) {SwingUtilities.invokeLater(()->click(f,text));}
    static void fill(JDialog d,String name,String email,String dept,String salary) throws Exception {
        edt(()->{var fields=children(d).stream().filter(c->c instanceof JTextField).map(c->(JTextField)c).toList();String[] values={name,email,dept,salary};for(int i=0;i<4;i++)fields.get(i).setText(values[i]);return null;});
    }
    static void close(JDialog d,String button) {SwingUtilities.invokeLater(()->click(d,button));}
    public static void main(String[] args) throws Exception {
        try {
            EmployeeApp.main(new String[0]);JFrame f=null;
            for(int i=0;i<100 && f==null;i++){f=edt(()->Arrays.stream(Frame.getFrames()).filter(w->w instanceof JFrame && w.isVisible()).map(w->(JFrame)w).findFirst().orElse(null));Thread.sleep(50);}
            if(f==null)throw new AssertionError("No window");final JFrame frame=f;ready(f);
            JTable table=edt(()->children(frame).stream().filter(c->c instanceof JTable).map(c->(JTable)c).findFirst().orElseThrow());
            open(f,"Add");var d=dialog("Add employee");fill(d,"Ada Lovelace","ada@example.org","Engineering","2500.00");close(d,"OK");Thread.sleep(150);ready(f);
            if(edt(table::getRowCount)!=1)throw new AssertionError("Add failed");
            edt(()->{table.setRowSelectionInterval(0,0);return null;});open(f,"Edit selected");d=dialog("Edit employee");fill(d,"Ada Lovelace","ada@example.org","Product","3000.00");close(d,"OK");Thread.sleep(150);ready(f);
            if(!edt(()->table.getValueAt(0,3)).equals("Product"))throw new AssertionError("Edit failed");
            open(f,"Add");d=dialog("Add employee");fill(d,"Zoë Müller","zoe@example.org","Engineering","2800.00");close(d,"OK");Thread.sleep(150);ready(f);
            edt(()->{table.getRowSorter().toggleSortOrder(1);table.getRowSorter().toggleSortOrder(1);table.setRowSelectionInterval(0,0);return null;});
            open(f,"Delete selected");d=dialog("Confirm deletion");close(d,"No");Thread.sleep(150);if(edt(table::getRowCount)!=2)throw new AssertionError("Cancel deletion failed");
            Path screenshot=Path.of(args.length>0?args[0]:"target/ui-smoke.png");Files.createDirectories(screenshot.toAbsolutePath().getParent());
            edt(()->{var img=new BufferedImage(frame.getWidth(),frame.getHeight(),BufferedImage.TYPE_INT_RGB);var g=img.createGraphics();frame.paint(g);g.dispose();ImageIO.write(img,"png",screenshot.toFile());return null;});
            open(f,"Delete selected");d=dialog("Confirm deletion");close(d,"Yes");Thread.sleep(150);ready(f);
            if(edt(table::getRowCount)!=1 || !edt(()->table.getValueAt(0,1)).equals("Ada Lovelace"))throw new AssertionError("Sorted selection deletion failed");
            System.out.println("UI smoke passed: launch, add, edit, sorted selection, cancel/confirm delete.");System.exit(0);
        } catch(Throwable ex){ex.printStackTrace();System.exit(1);}
    }
}
