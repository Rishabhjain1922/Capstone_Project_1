const API_BASE = 'https://capstone-project-1-dcit.onrender.com';


// Get the current attendee username from local storage which is stored during login
let currentAttendee = localStorage.getItem('attendeeUsername');


// Check if the user is logged in
document.addEventListener('DOMContentLoaded', () => {
    if (!currentAttendee) {
        window.location.href = 'login.html';
        return;
    }
    loadEvents();
    // Add event listener for search button
    document.getElementById('searchButton').addEventListener('click', performSearch);

    // Add event listener for Enter key in search input
    document.getElementById('searchInput').addEventListener('keypress', (e) => {
        if (e.key === 'Enter') {
            performSearch();
        }
    });
});


// Showing the loader while loading the events
function showLoading(show) {
    const loader = document.getElementById('loadingIndicator');
    if (loader) {
        loader.style.display = show ? 'block' : 'none';
    }
}

// Function to perform search
async function performSearch() {
    const searchTerm = document.getElementById('searchInput').value.trim();
    if (!searchTerm) {
        loadEvents();
        return;
    }

    try {
        showLoading(true);
        const [upcomingResponse, bookedResponse, deletedResponse] = await Promise.all([
            fetch(`${API_BASE}/events/attendee/search?attendeeUsername=${currentAttendee}&searchTerm=${encodeURIComponent(searchTerm)}`),
            fetch(`${API_BASE}/events/attendee/search-booked?attendeeUsername=${currentAttendee}&searchTerm=${encodeURIComponent(searchTerm)}`),
            fetch(`${API_BASE}/events/attendee/search-deleted?attendeeUsername=${currentAttendee}&searchTerm=${encodeURIComponent(searchTerm)}`)
        ]);

        if (!upcomingResponse.ok) throw new Error('Failed to search upcoming events');
        if (!bookedResponse.ok) throw new Error('Failed to search booked events');
        if (!deletedResponse.ok) throw new Error('Failed to search deleted events');

        const upcomingEvents = await upcomingResponse.json();
        const bookedEvents = await bookedResponse.json();
        const deletedEvents = await deletedResponse.json();

        renderEvents(upcomingEvents, bookedEvents, deletedEvents);
    } catch (error) {
        showError(error.message);
    } finally {
        showLoading(false);
    }
}

// Function to create a card for deleted events
function createDeletedEventCard(event) {
    return `
        <div class="col-md-4 mb-4">
            <div class="card event-card-attendee deleted-event">
                <div class="event-status status-deleted">
                    <i class="fas fa-trash-alt me-2"></i> Event Deleted
                </div>
                <img src="${event.eventImage || 'default-image.jpg'}" 
                     class="card-img-top" 
                     alt="${event.title}"
                     style="height: 200px; object-fit: cover; width: 100%;">
                <div class="card-body">
                    <h5 class="card-title">${event.title}</h5>
                    <p class="card-text">${event.description}</p>
                    <div class="event-meta">
                        <p><i class="fas fa-building"></i> ${event.organizerUsername}</p>
                        <p><i class="fas fa-calendar-alt"></i> ${new Date(event.eventDate).toLocaleString()}</p>
                        <p><i class="fas fa-ticket-alt"></i> Seat: ${event.seatsSold}/${event.totalSeats}</p>
                        <p><i class="fas fa-rupee-sign"></i> ${event.isFree ? 'FREE' : event.price.toFixed(2)}</p>
                    </div>
                    <div class="alert alert-warning">
                        <i class="fas fa-exclamation-circle me-2"></i>
                        This event has been canceled by the organizer
                    </div>
                </div>
            </div>
        </div>
    `;
}

// Function to show the loading indicator
async function loadEvents() {
    try {
        showLoading(true);
        const [upcomingResponse, bookedResponse, deletedResponse] = await Promise.all([
            fetch(`${API_BASE}/events/attendee/upcoming?attendeeUsername=${currentAttendee}`),
            fetch(`${API_BASE}/events/attendee/booked?attendeeUsername=${currentAttendee}`),
            fetch(`${API_BASE}/events/attendee/deleted?attendeeUsername=${currentAttendee}`)
        ]);

        if (!upcomingResponse.ok) throw new Error('Failed to load upcoming events');
        if (!bookedResponse.ok) throw new Error('Failed to load booked events');
        if (!deletedResponse.ok) throw new Error('Failed to load deleted events');

        const upcomingEvents = await upcomingResponse.json();
        const bookedEvents = await bookedResponse.json();
        const deletedEvents = await deletedResponse.json();

        renderEvents(upcomingEvents, bookedEvents, deletedEvents);
    } catch (error) {
        showError(error.message);
    } finally {
        showLoading(false);
    }
}


// Function to render the events on the page
function renderEvents(upcoming, booked, deleted = []) {
    const upcomingContainer = document.getElementById('upcomingEventsContainer');
    const myEventsContainer = document.getElementById('myEventsContainer');
    const deletedContainer = document.getElementById('deletedEventsContainer');

    upcomingContainer.innerHTML = upcoming.map(event => createEventCard(event)).join('');
    myEventsContainer.innerHTML = booked.map(event => createMyEventCard(event)).join('');

    // Update badge counts
    document.getElementById('myEventsBadge').textContent = booked.length;
    document.getElementById('myEventsBadge').classList.toggle('d-none', booked.length === 0);

    document.getElementById('deletedEventsBadge').textContent = deleted.length;
    document.getElementById('deletedEventsBadge').classList.toggle('d-none', deleted.length === 0);

    // Only show deleted events section if there are any
    if (deleted.length > 0) {
        document.getElementById('deletedEvents').style.display = 'block';
        deletedContainer.innerHTML = deleted.map(event => createDeletedEventCard(event)).join('');
    } else {
        document.getElementById('deletedEvents').style.display = 'none';
    }
}



// Function to create a card for upcoming events
function createEventCard(event) {
    const now = new Date();
    const eventDate = new Date(event.eventDate);
    const timeLeft = calculateTimeLeft(now, eventDate);
    const isEventOver = timeLeft === "Event Over";
    const status = isEventOver ? "EVENT OVER" :
        (event.status === 'CANCELLED' ? 'CANCELLED' : '');

    return `
        <div class="col-md-4 mb-4">
            <div class="card event-card-attendee">
                ${status ? `<div class="event-status ${status === 'CANCELLED' ? 'status-canceled' : 'status-over'}">${status}</div>` : ''}
                <img src="${event.eventImage || 'default-image.jpg'}" 
                     class="card-img-top" 
                     alt="${event.title}"
                     style="height: 200px; object-fit: cover; width: 100%;">
                <div class="card-body">
                    <h5 class="card-title">${event.title}</h5>
                    <p class="card-text">${event.description}</p>
                    <div class="event-meta">
                        <p><i class="fas fa-building"></i> ${event.organizerUsername}</p>
                        <p><i class="fas fa-clock"></i> ${timeLeft}</p>
                        <p><i class="fas fa-ticket-alt"></i> ${event.totalSeats - event.seatsSold} seats left</p>
                        <p><i class="fas fa-rupee-sign"></i> ${event.isFree ? 'FREE' : event.price.toFixed(2)}</p>
                    </div>
                    <button class="btn btn-primary w-100 mt-auto" 
                            onclick="bookEvent(${event.id})"
                            ${event.seatsSold >= event.totalSeats || event.status === 'CANCELLED' || isEventOver ? 'disabled' : ''}>
                        ${event.seatsSold >= event.totalSeats ? 'Sold Out' : 'Book Now'}
                    </button>
                </div>
            </div>
        </div>
    `;
}


// Function to create a card for upcoming events
function createMyEventCard(event) {
    const now = new Date();
    const eventDate = new Date(event.eventDate);
    const timeLeft = calculateTimeLeft(now, eventDate);
    const isEventOver = timeLeft === "Event Over";
    const isDeleted = event.status === 'DELETED';
    const status = isDeleted ? "DELETED BY ORGANIZER" :
        isEventOver ? "EVENT OVER" :
            getStatusText(event);

    return `
        <div class="col-md-4 mb-4">
            <div class="card event-card-attendee ${isDeleted ? 'deleted-event' : ''}">
                <div class="event-status ${isDeleted ? 'status-deleted' : getStatusClass(event)}">${status}</div>
                <img src="${event.eventImage || 'default-image.jpg'}" 
                     class="card-img-top" 
                     alt="${event.title}"
                     style="height: 200px; object-fit: cover; width: 100%;">
                <div class="card-body">
                    <h5 class="card-title">${event.title}</h5>
                    ${event.status === 'UPDATED' ? `
                    <div class="alert alert-info mb-3">
                        <small><i class="fas fa-info-circle"></i> This event has been updated by the organizer</small>
                    </div>` : ''}
                    <p class="card-text">${event.description}</p>
                    <div class="event-meta">
                        <p><i class="fas fa-building"></i> ${event.organizerUsername}</p>
                        <p><i class="fas fa-clock"></i> ${timeLeft}</p>
                        <p><i class="fas fa-ticket-alt"></i> Seat: ${event.seatsSold}/${event.totalSeats}</p>
                        <p><i class="fas fa-rupee-sign"></i> ${event.isFree ? 'FREE' : event.price.toFixed(2)}</p>
                    </div>
                    ${!isDeleted && (event.status === 'ACTIVE' || event.status === 'UPDATED') ? `
                    <div class="d-grid gap-2 mt-auto">
                        <button class="btn btn-danger" onclick="cancelBooking(${event.id})">
                            Cancel Booking
                        </button>
                    </div>` : ''}
                </div>
            </div>
        </div>
    `;
}




// Function to calculate the time left until the event
function calculateTimeLeft(now, eventDate) {
    if (now >= eventDate) {
        return "Event Over";
    }

    const diff = eventDate - now;
    const days = Math.floor(diff / (1000 * 60 * 60 * 24));
    const hours = Math.floor((diff % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60));

    if (days > 0) {
        return `${days} day${days !== 1 ? 's' : ''} ${hours} hr${hours !== 1 ? 's' : ''} left`;
    } else if (hours > 0) {
        return `${hours} hr${hours !== 1 ? 's' : ''} left`;
    } else {
        return "Less than 1 hour left";
    }
}


// Function to get the status class based on event status
function getStatusClass(event) {
    switch(event.status) {
        case 'CANCELLED': return 'status-canceled';
        case 'UPDATED': return 'status-updated';
        default: return 'status-booked';
    }
}

// Function to get the status text based on event status
function getStatusText(event) {
    switch(event.status) {
        case 'CANCELLED': return 'CANCELLED BY ORGANIZER';
        case 'UPDATED': return 'DETAILS UPDATED';
        default: return 'BOOKED';
    }
}


// Function to book an event
async function bookEvent(eventId) {
    try {
        const response = await fetch(
            `${API_BASE}/events/${eventId}/book?attendeeUsername=${currentAttendee}`,
            { method: 'POST' }
        );

        if (!response.ok) throw new Error('Booking failed');

        loadEvents();
        showSuccess('Booking successful!');
    } catch (error) {
        showError(error.message);
    }
}


// Function to cancel a booking
async function cancelBooking(eventId) {
    try {
        const response = await fetch(
            `${API_BASE}/events/${eventId}/cancel?attendeeUsername=${currentAttendee}`,
            { method: 'POST' }
        );

        if (!response.ok) throw new Error('Cancel failed');

        loadEvents();
        showSuccess('Booking canceled!');
    } catch (error) {
        showError(error.message);
    }
}


// Function to show the selected section and hide others
function showSection(sectionId) {
    // Hide all sections
    document.querySelectorAll('.events-section').forEach(section => {
        section.style.display = 'none';
    });

    // Show the selected section
    document.getElementById(sectionId).style.display = 'block';

    // Update active nav item
    document.querySelectorAll('.nav-item').forEach(item => {
        item.classList.remove('active');
    });

    // Find and activate the clicked nav item
    const navItems = document.querySelectorAll('.nav-item');
    for (let i = 0; i < navItems.length; i++) {
        if (navItems[i].getAttribute('onclick')?.includes(sectionId)) {
            navItems[i].classList.add('active');
            break;
        }
    }
}


// Function to show a success message
function showSuccess(message) {
    Swal.fire({ icon: 'success', title: 'Success!', text: message, timer: 2000 });
}


// Function to show an error message
function showError(message) {
    Swal.fire({ icon: 'error', title: 'Error!', text: message, timer: 3000 });
}


// Function to log out the user
function logout() {
    localStorage.removeItem('attendeeUsername');
    window.location.href = 'login.html';
}