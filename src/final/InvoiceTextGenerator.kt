package final


data class ShipmentItem(
    val productID: String,
    val quantity: Int,
    val weight: Double
)

data class Order(
    val customerName: String,
    val shipmentItems: List<ShipmentItem>
)

data class Product(
    val name: String,
    val shippingMethod: String
)

class InvoiceTextGenerator(val order: Order, val products: Map<String, Product>) {

    fun generate(): String {
        var totalCost = 0
        var loyaltyPoints = 0
        var result = "Shipping Invoice for ${order.customerName}\n"

        for (item in order.shipmentItems) {
            loyaltyPoints += calcLoyaltyPointsIncrease(item)
            result += getInoviceForLineItem(getProduct(item), calcItemCost(item), item)
            totalCost += calcItemCost(item)
        }

        result += "Total shipping cost is ${formatCurrency(totalCost)}\n"
        result += "You earned $loyaltyPoints loyalty points\n"

        return result
    }

    private fun getProduct(item: ShipmentItem): Product = products[item.productID]!!

    private fun getInoviceForLineItem(product: Product, itemCost: Int, item: ShipmentItem): String =
        "  ${product.name}: ${formatCurrency(itemCost)} " +
                "(${item.quantity} items, ${item.weight}kg)\n"

    private fun calcLoyaltyPointsIncrease(item: ShipmentItem): Int {
        val product = getProduct(item)
        var loyaltyPoints = maxOf(item.quantity - 2, 0)
        if ("express" == product.shippingMethod) {
            loyaltyPoints += item.quantity / 3
        }
        return loyaltyPoints
    }

    private fun calcItemCost(item: ShipmentItem): Int {
        val product = getProduct(item)
        var itemCost = 0
        when (product.shippingMethod) {
            "standard" -> {
                itemCost = 500
                if (item.weight > 5.0) {
                    itemCost += (100 * (item.weight - 5.0)).toInt()
                }
            }

            "express" -> {
                itemCost = 1200
                if (item.weight > 3.0) {
                    itemCost += (250 * (item.weight - 3.0)).toInt()
                }

                itemCost += 150 * item.quantity
            }

            else -> {
                throw kotlin.IllegalArgumentException("unknown shipping method: ${product.shippingMethod}")
            }
        }
        return itemCost
    }

    fun formatCurrency(amountInCents: Int): String {
        val amountInDollars = amountInCents / 100.0
        return "$%.2f".format(amountInDollars)
    }
}

