package com.ryan.vault.libs.utility;

import com.ryan.vault.libs.base.Base;

public class CleanInputs extends Base {
    private String setLowerCase(String site) {
        return site.toLowerCase();
    }

    private String removeWhiteSpace(String site) {
        return site.replaceAll(" ", "");
    }

    public String setSiteString(String site) {
        String lowerCaseString = setLowerCase(site);

        return removeWhiteSpace(lowerCaseString);
    }
}
