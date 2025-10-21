import java.io.File;
public class listfiles {
    public listfiles (File[] filelist, String indent){
        for (File f: filelist){
            System.out.println(indent + "|--"+ f.getName());
            if(f.isDirectory()){
                String h = f.getAbsolutePath();
                File subfile = new File(h);
                File[] subfilelist = subfile.listFiles();
                new listfiles (subfilelist, indent+"|    ");
            }
        }
    }
}
