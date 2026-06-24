package api.auto.generate.table.utill;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FileHandling<T, A> {
    private final File file;
    private final Class<A> clazz;

    public FileHandling(String path, Class<A> clazz){
        this.clazz = clazz;
        this.file = new File(path);
        if (!file.exists()) {
            try {
                boolean f = file.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public boolean save(List<T> list, boolean append) {
        if (append) {
            List<T> existingProfiles = read();
            existingProfiles.add(list.getFirst());
            return write(existingProfiles);
        }
        return write(list);
    }

    private boolean write(List<T> list) {
        try {
            ObjMapper.mapper.writeValue(file, list);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public  List<T> read() {
        try {
            A data = ObjMapper.mapper.readValue(file, clazz);
            return new ArrayList<>(List.of((T[]) data));
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }


}
