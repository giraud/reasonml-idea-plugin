package com.reason.build.annotations;

import javax.annotation.Nullable;
import java.util.Collection;

public interface ErrorsManager {

    void put(@Nullable OutputInfo error);

    Collection<OutputInfo> getErrors(String filePath);

    void clearErrors();

}
