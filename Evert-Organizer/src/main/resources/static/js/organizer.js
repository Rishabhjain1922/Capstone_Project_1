const API_BASE = 'http://localhost:8080/api';
let selectedEventId = null;
let DEFAULT_IMAGE_URL = 'https://localmedia.org/wp-content/uploads/2021/06/events-1.png';


// Initialize the page for the organizer
document.addEventListener('DOMContentLoaded', () => {
    const organizerUsername = localStorage.getItem('organizerUsername');
    if (!organizerUsername) {
        window.location.href = 'login.html';
        return;
    }
    loadOrganizerEvents(organizerUsername);
});

// Show loading indicator for fetch requests
function showLoadingIndicator(show) {
    document.getElementById('loadingIndicator').style.display = show ? 'block' : 'none';
}
// Load organizer's events
async function loadOrganizerEvents(organizerUsername) {
    try {
        showLoadingIndicator(true);
        const response = await fetch(`${API_BASE}/events?organizerUsername=${encodeURIComponent(organizerUsername)}`);

        if (!response.ok) throw new Error(`HTTP error! status: ${response.status}`);

        const events = await response.json();
        renderEvents(events);
    } catch (error) {
        showErrorAlert(error.message || 'Failed to load events');
    } finally {
        showLoadingIndicator(false);
    }
}

// Render events to the page and adding the event cards based on the event data
function renderEvents(events) {
    const container = document.getElementById('eventsContainer');
    container.innerHTML = '';

    if (events.length === 0) {
        container.innerHTML = `
            <div class="col-12">
                <div class="alert alert-info">
                    No events found. Start by creating your first event!
                </div>
            </div>
        `;
        return;
    }

    events.forEach(event => {
        const eventCard = createEventCard(event);
        container.appendChild(eventCard);
    });
}

// Create event card element for each event
function createEventCard(event) {
    const now = new Date();
    const eventDate = new Date(event.eventDate);
    const timeLeft = calculateTimeLeft(now, eventDate);
    const isEventOver = timeLeft === "Event Over";
    const status = isEventOver ? "EVENT OVER" :
        (event.status === 'CANCELLED' ? 'CANCELLED' :
            (event.status === 'UPDATED' ? 'UPDATED' : ''));

    const card = document.createElement('div');
    card.className = 'col-md-4 mb-4';
    card.innerHTML = `
      <div class="card event-card light-card ${event.id === selectedEventId ? 'selected' : ''}" 
     onclick="toggleEventSelection(${event.id})">
        ${status ? `<div class="event-status ${status === 'CANCELLED' ? 'status-canceled' :
        status === 'UPDATED' ? 'status-updated' : 'status-over'}">${status}</div>` : ''}
        <img src="${event.eventImage || DEFAULT_IMAGE_URL}" 
             class="card-img-top" 
             alt="${event.title}"
             style="height: 200px; object-fit: cover;">
        <div class="card-body">
            <h5 class="card-title">${event.title}</h5>
            <p class="card-text">${event.description}</p>
            <div class="event-details">
                <p><i class="fas fa-calendar-alt"></i> ${new Date(event.eventDate).toLocaleString()}</p>
                <p><i class="fas fa-chair"></i> Seats: ${event.seatsSold}/${event.totalSeats}</p>
                <p><i class="fas fa-tag"></i> ₹${event.price.toFixed(2)}</p>
            </div>
            <div class="mt-3 d-flex justify-content-end gap-2">
                <button class="btn btn-sm btn-warning" onclick="event.stopPropagation(); editSelectedEvent(${event.id})">
                    <i class="fas fa-edit"></i> Edit
                </button>
                <button class="btn btn-sm btn-danger" onclick="event.stopPropagation(); deleteSelectedEvent(${event.id})">
                    <i class="fas fa-trash"></i> Delete
                </button>
            </div>
        </div>
    </div>
    `;
    return card;
}

// Add new event modal
async function addNewEvent() {
    const organizerUsername = localStorage.getItem('organizerUsername');
    if (!organizerUsername) {
        showErrorAlert('Organizer not recognized. Please login again.');
        return;
    }

    const form = document.getElementById('addEventForm');
    const formData = new FormData(form);

    try {
        // Convert FormData to object with proper type conversions
        const eventData = {
            organizerUsername: organizerUsername,
            eventImage: formData.get('eventImage') || DEFAULT_IMAGE_URL,
            eventType: formData.get('eventType'),
            title: formData.get('title'),
            description: formData.get('description'),
            eventDate: new Date(formData.get('eventDate')).toISOString(),
            totalSeats: parseInt(formData.get('totalSeats')),
            price: parseFloat(formData.get('price')) || 0,
            free: formData.get('isFree') === 'true'
        };

        console.log('Submitting event data:', eventData); // Debug log

        const response = await fetch(`${API_BASE}/events`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',

            },
            body: JSON.stringify(eventData)
        });

        const responseData = await response.json();
        console.log('Server response:', responseData); // Debug log

        if (!response.ok) {
            const errorMessage = responseData.message || 'Failed to create event';
            throw new Error(`${errorMessage} (Status: ${response.status})`);
        }

        // Close modal and refresh list
        const modal = bootstrap.Modal.getInstance(document.getElementById('addEventModal'));
        modal.hide();
        await loadOrganizerEvents(organizerUsername);
        form.reset();
        showSuccessAlert('Event created successfully!');

    } catch (error) {
        console.error('Event creation error:', error);
        showErrorAlert(`Event creation failed: ${error.message}`);
    }
}

// Edit event modal
function showEditModal(event) {
    const modalContent = document.getElementById('editEventContent');
    modalContent.innerHTML = `
        <form id="editEventForm">
            <input type="hidden" name="eventId" value="${event.id}">
            <input type="hidden" name="status" value="UPDATED">
            <div class="mb-3">
                <label class="form-label">Event Image URL</label>
                <input type="text" class="form-control" name="eventImage" value="${event.eventImage || ''}">
            </div>
            <div class="mb-3">
                <label class="form-label">Event Type</label>
                <select class="form-select" name="eventType" required>
                    <option value="CONFERENCE" ${event.eventType === 'CONFERENCE' ? 'selected' : ''}>Conference</option>
                    <option value="WORKSHOP" ${event.eventType === 'WORKSHOP' ? 'selected' : ''}>Workshop</option>
                    <option value="SEMINAR" ${event.eventType === 'SEMINAR' ? 'selected' : ''}>Seminar</option>
                    <option value="CELEBRATION" ${event.eventType === 'CELEBRATION' ? 'selected' : ''}>Celebration</option>
                </select>
            </div>
            <div class="mb-3">
                <label class="form-label">Event Title</label>
                <input type="text" class="form-control" name="title" value="${event.title}" required>
            </div>
            <div class="mb-3">
                <label class="form-label">Description</label>
                <textarea class="form-control" name="description" rows="3" required>${event.description}</textarea>
            </div>
            <div class="row">
                <div class="col-md-6 mb-3">
                    <label class="form-label">Date & Time</label>
                    <input type="datetime-local" class="form-control" name="eventDate" 
                           value="${formatDateTimeForInput(event.eventDate)}" required>
                </div>
              
            </div>
            <div class="row">
                <div class="col-md-6 mb-3">
                    <div class="form-check">
                        <input class="form-check-input" type="checkbox" name="isFree" id="editIsFree" 
                               ${event.free ? 'checked' : ''} value="true">
                        <label class="form-check-label" for="editIsFree">Free Event</label>
                    </div>
                    <input type="hidden" name="isFree" value="false">
                </div>
                <div class="col-md-6 mb-3">
                    <label class="form-label">Price per Ticket ($)</label>
                    <input type="number" class="form-control" name="price" id="eventPrice"
                           value="${event.free ? '0.00' : event.price.toFixed(2)}" 
                           min="0" step="0.01" ${event.free ? 'disabled' : 'required'}>
                </div>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Close</button>
                <button type="button" class="btn btn-primary" id="saveChangesBtn">Save Changes</button>
            </div>
        </form>
    `;

    const modal = new bootstrap.Modal(document.getElementById('editEventModal'));
    modal.show();

    // Handle free checkbox change
    const freeCheckbox = document.getElementById('editIsFree');
    const priceInput = document.getElementById('eventPrice');

    freeCheckbox.addEventListener('change', function() {
        if (this.checked) {
            priceInput.disabled = true;
            priceInput.value = '0.00';
        } else {
            priceInput.disabled = false;
        }
    });

    // Save changes handler
    document.getElementById('saveChangesBtn').addEventListener('click', async function() {
        try {
            await updateEvent();
            modal.hide();
        } catch (error) {
            console.error('Error updating event:', error);
        }
    });
}

// Updated editSelectedEvent
async function editSelectedEvent(eventId) {
    const organizerUsername = localStorage.getItem('organizerUsername');
    try {
        const response = await fetch(
            `${API_BASE}/events/${eventId}?organizerUsername=${encodeURIComponent(organizerUsername)}`
        );
        if (!response.ok) throw new Error('Event not found');
        const event = await response.json();
        selectedEventId = eventId;
        showEditModal(event);
    } catch (error) {
        showErrorAlert(error.message);
    }
}

// Delete selected event in the organizer's events
async function deleteSelectedEvent(eventId) {
    const organizerUsername = localStorage.getItem('organizerUsername');
    try {
        const result = await Swal.fire({
            title: 'Are you sure?',
            text: "The event will be marked as deleted but kept in the system.",
            icon: 'warning',
            showCancelButton: true,
            confirmButtonColor: '#3085d6',
            cancelButtonColor: '#d33',
            confirmButtonText: 'Yes, mark as deleted'
        });

        if (result.isConfirmed) {
            const response = await fetch(
                `${API_BASE}/events/${eventId}?organizerUsername=${encodeURIComponent(organizerUsername)}`,
                { method: 'DELETE' }
            );

            if (!response.ok) throw new Error('Failed to delete event');

            await loadOrganizerEvents(organizerUsername);
            showSuccessAlert('Event marked as deleted!');
        }
    } catch (error) {
        showErrorAlert(error.message);
    }
}

// Calculate time left until the event
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

// Updated updateEvent function
async function updateEvent() {
    const organizerUsername = localStorage.getItem('organizerUsername');
    const form = document.getElementById('editEventForm');
    const formData = new FormData(form);
    const isFree = formData.get('isFree') === 'true';

    try {
        // Get current event data first to store original details
        const currentEventResponse = await fetch(`${API_BASE}/events/${selectedEventId}`);
        const currentEvent = await currentEventResponse.json();

        const eventData = {
            organizerUsername: organizerUsername,
            eventImage: formData.get('eventImage'),
            eventType: formData.get('eventType'),
            title: formData.get('title'),
            description: formData.get('description'),
            eventDate: new Date(formData.get('eventDate')).toISOString(),
            totalSeats: parseInt(formData.get('totalSeats')),
            price: isFree ? 0 : parseFloat(formData.get('price')),
            free: isFree,
            status: 'UPDATED',
            originalDetails: JSON.stringify({
                title: currentEvent.title,
                description: currentEvent.description,
                eventDate: currentEvent.eventDate,
                totalSeats: currentEvent.totalSeats,
                price: currentEvent.price
            })
        };

        const response = await fetch(
            `${API_BASE}/events/${selectedEventId}`,
            {
                method: 'PUT',
                headers: {'Content-Type': 'application/json'},
                body: JSON.stringify(eventData)
            }
        );

        if (!response.ok) {
            const errorData = await response.json();
            throw new Error(errorData.message || 'Failed to update event');
        }

        await loadOrganizerEvents(organizerUsername);
        showSuccessAlert('Event updated successfully!');
    } catch (error) {
        showErrorAlert(error.message);
        throw error;
    }
}
// toogle the event  card
function toggleEventSelection(eventId) {
    selectedEventId = selectedEventId === eventId ? null : eventId;
    document.querySelectorAll('.event-card').forEach(card => {
        card.classList.toggle('selected', card.dataset.eventId === eventId);
    });
}


// Format date and time for input field
function formatDateTimeForInput(isoString) {
    const date = new Date(isoString);
    return date.toISOString().slice(0, 16);
}



//
function showSuccessAlert(message) {
    Swal.fire({
        icon: 'success',
        title: 'Success!',
        text: message,
        timer: 2000,
        showConfirmButton: false
    });
}


// Show error alert using SweetAlert2
function showErrorAlert(message) {
    Swal.fire({
        icon: 'error',
        title: 'Error!',
        text: message,
        timer: 3000
    });
}