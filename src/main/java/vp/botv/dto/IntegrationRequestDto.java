package vp.botv.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class IntegrationRequestDto {

    @JsonProperty("email")
    private String email;

    @JsonProperty("id")
    private String id;

    @JsonProperty("exp_time")
    private String expTime;

    @JsonProperty("limit_ip")
    private Integer limit_ip;

    @JsonProperty("total_gb")
    private Integer total_gb;
}
