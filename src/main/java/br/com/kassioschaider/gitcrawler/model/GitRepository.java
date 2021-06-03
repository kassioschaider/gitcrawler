package br.com.kassioschaider.gitcrawler.model;

import lombok.*;

import java.io.Serializable;
import java.util.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@ToString
public class GitRepository implements Serializable {

    private static final long serialVersionUID = 1L;

    private String linkRepository;
    private final Set<DataGitFile> dataGitFiles = Collections.synchronizedSet(new HashSet<>());
    private final Map<String, DataGitFile> extensionToDataGitFile = Collections.synchronizedMap(new HashMap<>());

    public synchronized void addDataGitFile(DataGitFile dataGitFile) {
        if(!dataGitFiles.contains(dataGitFile)) {
            extensionToDataGitFile.put(dataGitFile.getExtension(), dataGitFile);
            dataGitFile.setCount(1);
            dataGitFiles.add(dataGitFile);
            return;
        }

        sumValuesDataGitFile(getDataGitFileByExtension(dataGitFile.getExtension()), dataGitFile);
    }

    private synchronized void sumValuesDataGitFile(DataGitFile data, DataGitFile newData) {
        data.setLines(data.getLines() + newData.getLines());
        data.setBytes(data.getBytes().add(newData.getBytes()));
        data.setCount(data.getCount() + 1);
    }

    private synchronized DataGitFile getDataGitFileByExtension(String extension) {
        return extensionToDataGitFile.get(extension);
    }
}
