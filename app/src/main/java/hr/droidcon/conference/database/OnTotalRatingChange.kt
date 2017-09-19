package hr.droidcon.conference.database

interface OnTotalRatingChange {
    fun onChange(rating: Double, count: Int)
}
