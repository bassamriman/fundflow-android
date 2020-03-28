package com.rimanware.fundflow_android

import arrow.core.Option
import arrow.core.getOption
import fundflow.Fund
import fundflow.FundRef

object DataManager {
    private var funds: Map<FundRef, Fund> = emptyMap()

    init {
        funds = initialFunds()
    }

    private fun initialFunds(): Map<FundRef, Fund> {
        val carFund = Fund("Car Fund", "Money saved for car")
        val homeFund = Fund("Home Fund", "Money saved for home")

        return funds + Pair(carFund.reference, carFund) + Pair(homeFund.reference, homeFund)
    }

    fun funds(): List<Fund> = funds.values.toList()
    fun getFundByRefId(id: String): Option<Fund> = getFundByRef(FundRef(id))
    fun getFundByRef(ref: FundRef): Option<Fund> = funds.getOption(ref)
    fun save(fund: Fund) {
        funds = funds + Pair(fund.reference, fund)
    }
}