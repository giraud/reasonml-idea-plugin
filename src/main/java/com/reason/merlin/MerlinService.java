package com.reason.merlin;

import com.reason.merlin.types.*;

import java.util.List;

public interface MerlinService {
    MerlinVersion selectVersion(int version);

    List<MerlinError> errors(String filename, String source);

    List<MerlinType> typeExpression(String filename, String source, MerlinPosition position);

    MerlinCompletion completions(String filename, String source, MerlinPosition position, String prefix);
}
