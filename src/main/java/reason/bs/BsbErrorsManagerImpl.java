package reason.bs;

import com.intellij.openapi.components.ProjectComponent;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.intellij.psi.PsiElement;
import com.intellij.util.containers.ConcurrentMultiMap;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Iterator;

public class BsbErrorsManagerImpl extends BsbErrorsManager implements ProjectComponent {

    private ConcurrentMultiMap<String, BsbError> m_errorsByFile;

    @NotNull
    @Override
    public String getComponentName() {
        return BsbErrorsManager.class.getSimpleName();
    }

    @Override
    public void projectOpened() {
        m_errorsByFile = new ConcurrentMultiMap<>();
    }

    @Override
    public void projectClosed() {
        m_errorsByFile = null;
    }

    @Override
    public void initComponent() {

    }

    @Override
    public void disposeComponent() {

    }

    @Override
    public void setError(String filePath, BsbError error) {
        VirtualFile fileByUrl = VirtualFileManager.getInstance().findFileByUrl("file://" + filePath);
        if (fileByUrl != null) {
            m_errorsByFile.putValue(fileByUrl.getCanonicalPath(), error);
        }
    }

    @Override
    public Collection<BsbError> getErrors(String filePath) {
        return m_errorsByFile.get(filePath);
    }

    @Override
    public void clearErrors() {
        m_errorsByFile.clear();
    }

    @Override
    public void associatePsiElement(VirtualFile virtualFile, PsiElement elementAtOffset) {
        Iterator<BsbError> itErrors = m_errorsByFile.get(virtualFile.getCanonicalPath()).iterator();
        if (itErrors.hasNext()) {
            // This is not correct
            itErrors.next().element = elementAtOffset;
        }
    }

}
