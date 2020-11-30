/*
 * Copyright (c) 2020. Byoskill Leroy (Sylvain Leroy).
 * All rights reserved.
 */

package com.byoskill.tools.springcrudgenerator.restgenerator;

import com.byoskill.tools.springcrudgenerator.rapid.catalog.ProjectInformation;
import lombok.Data;

@Data
public class RestProjectInformation implements ProjectInformation {
    public String projectFolder;
    public String modelFolder;
    public String processorFolder;
    public String restFolder;

}
