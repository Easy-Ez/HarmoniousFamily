package com.gh0u1l5.wechatmagician.spellbook.data

import com.gh0u1l5.wechatmagician.spellbook.Predicate

data class Record(
    // The variable sections records the sections of items we want to show
    @Volatile var sections: List<Section>,
    // The variable predicates records the predicates of the adapters.
    // An item will be hidden if it satisfies any one of the predicates.
    @Volatile var predicates: Map<String, Predicate>
)