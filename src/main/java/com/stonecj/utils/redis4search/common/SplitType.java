package com.stonecj.utils.redis4search.common;

import lombok.AllArgsConstructor;

/**
 * @author sotnecj
 * @mail lpf599@163.com
 */
@AllArgsConstructor
public enum SplitType {

    PerChar("逐字分割"),
    WithTool("工具分割");

    private String desc;
}
