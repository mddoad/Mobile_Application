package com.example.universityeventapp

object SampleData {
    // Using built-in icons so the project compiles without adding images.
    private val img1 = android.R.drawable.ic_menu_gallery
    private val img2 = android.R.drawable.ic_menu_camera
    private val img3 = android.R.drawable.ic_menu_compass
    private val img4 = android.R.drawable.ic_menu_agenda

    val events: List<Event> = listOf(
        Event(
            id = 1,
            title = "Tech Talk: Future of AI",
            date = "Apr 12, 2026",
            time = "10:00 AM",
            venue = "Auditorium A",
            category = "Tech",
            description = longDesc("AI is transforming industries and classrooms."),
            price = 2.99,
            totalSeats = 48,
            availableSeats = 34,
            imageRes = img1
        ),
        Event(
            id = 2,
            title = "Inter-Department Sports Day",
            date = "Apr 18, 2026",
            time = "9:00 AM",
            venue = "Main Ground",
            category = "Sports",
            description = longDesc("A full-day sports event with multiple games."),
            price = 0.0,
            totalSeats = 48,
            availableSeats = 30,
            imageRes = img2
        ),
        Event(
            id = 3,
            title = "Cultural Night 2026",
            date = "Apr 25, 2026",
            time = "6:30 PM",
            venue = "Open Air Theatre",
            category = "Cultural",
            description = longDesc("Music, drama, and performances by student clubs."),
            price = 1.49,
            totalSeats = 48,
            availableSeats = 28,
            imageRes = img3
        ),
        Event(
            id = 4,
            title = "Academic Workshop: Research Writing",
            date = "May 2, 2026",
            time = "11:00 AM",
            venue = "Lab 3",
            category = "Academic",
            description = longDesc("Improve your research writing and citation skills."),
            price = 0.99,
            totalSeats = 48,
            availableSeats = 22,
            imageRes = img4
        ),
        Event(
            id = 5,
            title = "Social Meetup: Freshers Welcome",
            date = "May 5, 2026",
            time = "3:00 PM",
            venue = "Student Lounge",
            category = "Social",
            description = longDesc("Meet your seniors and make new friends."),
            price = 0.0,
            totalSeats = 48,
            availableSeats = 40,
            imageRes = img1
        ),
        Event(
            id = 6,
            title = "Tech Workshop: Android Basics",
            date = "May 10, 2026",
            time = "2:00 PM",
            venue = "CS Lab 1",
            category = "Tech",
            description = longDesc("Hands-on Android development workshop."),
            price = 1.99,
            totalSeats = 48,
            availableSeats = 26,
            imageRes = img2
        ),
        Event(
            id = 7,
            title = "Sports: Badminton Tournament",
            date = "May 14, 2026",
            time = "1:00 PM",
            venue = "Indoor Hall",
            category = "Sports",
            description = longDesc("Singles and doubles tournament. Register early!"),
            price = 0.0,
            totalSeats = 48,
            availableSeats = 20,
            imageRes = img3
        ),
        Event(
            id = 8,
            title = "Academic Seminar: Career Planning",
            date = "May 20, 2026",
            time = "12:00 PM",
            venue = "Seminar Room B",
            category = "Academic",
            description = longDesc("Guidance on internships, CVs, and interviews."),
            price = 0.0,
            totalSeats = 48,
            availableSeats = 36,
            imageRes = img4
        )
    )

    private fun longDesc(prefix: String): String {
        // Long enough for detail screen scroll
        return "$prefix\n\n" +
                "This event is designed for university students and includes interactive segments, Q&A, and practical takeaways. " +
                "Participants will learn key concepts, get networking opportunities, and receive resources to continue learning. " +
                "Please arrive 10 minutes early for registration. Seats are limited and will be allocated on a first-come basis. " +
                "By attending, you agree to follow campus conduct and safety guidelines."
    }
}