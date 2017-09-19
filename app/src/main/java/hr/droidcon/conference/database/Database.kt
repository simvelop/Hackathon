package hr.droidcon.conference.database

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

private const val RATINGS = "ratings"
private const val FAVORITES = "favorites"

private val db: DatabaseReference
    get() = FirebaseDatabase.getInstance().reference

private fun valueEventListener(onDataChange: (DataSnapshot) -> Unit): ValueEventListener =
        object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {}
            override fun onDataChange(snapshot: DataSnapshot) = onDataChange(snapshot)
        }

private fun overallRating(conferenceId: String): DatabaseReference =
        db.child(RATINGS).child(conferenceId)

private fun userRating(conferenceId: String, deviceId: String): DatabaseReference =
        overallRating(conferenceId).child(deviceId)

private fun userFavorite(conferenceId: String, deviceId: String): DatabaseReference =
        db.child(FAVORITES).child(conferenceId).child(deviceId)

fun setRating(conferenceId: String, deviceId: String, rating: Double) {
    userRating(conferenceId, deviceId).setValue(rating)
}

fun onUserRatingChange(conferenceId: String, deviceId: String, listener: OnUserRatingChange) {
    userRating(conferenceId, deviceId).addValueEventListener(valueEventListener {
        (it.value as? Number)?.toDouble()?.let(listener::onChange)
    })
}

fun onOverallRatingChange(conferenceId: String, listener: OnTotalRatingChange) {
    overallRating(conferenceId).addValueEventListener(valueEventListener {
        val ratings = it.children.mapNotNull { it.value as? Number }.map(Number::toDouble)
        listener.onChange(ratings.average(), ratings.count())
    })
}

fun setFavorite(conferenceId: String, deviceId: String, favorite: Boolean) {
    val fav = userFavorite(conferenceId, deviceId)
    if (favorite) {
        fav.setValue(true)
    } else {
        fav.removeValue()
    }
}
