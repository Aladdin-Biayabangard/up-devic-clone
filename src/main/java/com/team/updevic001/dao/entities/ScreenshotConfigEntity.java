package com.team.updevic001.dao.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "screenshot_config")
public class ScreenshotConfigEntity {

    @Id
    private String id;

    @NotBlank
    private String url;

    // If null/empty and type == "element", we’ll 400. Not needed for fullpage.
    private String selector;

    // "element" or "fullpage"
    private String type = "element";

    // Viewport + scale
    private Integer width = 1280;
    private Integer height = 1024;
    private Double scale = 1.5;

    // Waits
    private Long waitForMs = 300L;       // short settle delay after load
    private Long navTimeoutMs = 60000L;  // navigation timeout

    // Rendering
    private Boolean omitBackground = true; // PNG transparency
    private String format = "png";         // "png" or "jpeg"
    private Integer quality = 90;          // for jpeg only

}
