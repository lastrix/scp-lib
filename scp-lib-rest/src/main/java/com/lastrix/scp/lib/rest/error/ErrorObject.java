package com.lastrix.scp.lib.rest.error;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Error information")
public class ErrorObject {

    @Schema(description = "Unique error id (uuid)")
    private String id;

    @Schema(description = "Error code in format XXX_XXXX")
    private String code;

    @Schema(description = "Error common description (does not include any specific information, always constant, additional data in params)")
    private String description;

    @Schema(description = "Sensitive error data, or <hidden> if production mode")
    private String meta;

    @Schema(description = "Stack trace, or <hidden> if production mode")
    private List<String> stackTrace;

    @Schema(description = "Error parameters, such as person id or other vital information for error debugging or displaying, no sensitive data is allowed here")
    private Map<String, String> params;

    public String getParam(String key) {
        return params == null ? null : params.get(key);
    }

    public String requireParam(String key) {
        var p = getParam(key);
        if (p == null) {
            throw new IllegalArgumentException("No param for key: " + key);
        }
        return p;
    }
}
