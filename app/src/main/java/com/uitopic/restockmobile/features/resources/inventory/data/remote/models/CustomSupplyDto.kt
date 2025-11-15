import com.uitopic.restockmobile.features.resources.inventory.data.remote.models.SupplyDto

data class CustomSupplyDto(
    val id: Int?,
    val supply: SupplyDto?,
    val description: String?,
    val minStock: Int?,
    val maxStock: Int?,
    val price: Double?,
    val userId: Int?,
    val unitName: String?,
    val unitAbbreviaton: String?,
    val currencyCode: String?
)

data class CustomSupplyRequestDto(
    val id: Int? = null,
    val supplyId: Int,
    val description: String,
    val minStock: Int,
    val maxStock: Int,
    val price: Double,
    val userId: Int,
    val unitName: String,
    val unitAbbreviaton: String
)
