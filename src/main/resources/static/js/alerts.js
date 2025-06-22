function initializeAlertSystem() {
    // Add CSS to document
    const style = document.createElement('style');
    style.textContent = `
        .alert-box {
            position: fixed;
            top: 20px;
            right: 20px;
            padding: 16px 40px 16px 16px;
            border-radius: 8px;
            font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif;
            font-size: 14px;
            display: flex;
            align-items: center;
            gap: 12px;
            box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
            animation: slideIn 0.3s ease-out;
            z-index: 1000;
            min-width: 300px;
            max-width: 450px;
            color: #ffffff; /* White text color for all alerts */
        }

        .alert-box .icon {
            flex-shrink: 0;
            width: 20px;
            height: 20px;
        }

        .alert-box .close-btn {
            position: absolute;
            right: 12px;
            top: 50%;
            transform: translateY(-50%);
            background: none;
            border: none;
            cursor: pointer;
            padding: 4px;
            opacity: 0.5;
        }

        .alert-box .close-btn:hover {
            opacity: 1;
        }

        .alert-success {
            background-color: #28a745; /* Green for success */
        }

        .alert-error {
            background-color: #dc3545; /* Red for error */
        }

        .alert-warning {
            background-color: #ffc107; /* Yellow for warning */
        }

        .alert-info {
            background-color: #17a2b8; /* Blue for info */
        }

        @keyframes slideIn {
            from {
                transform: translateX(100%);
                opacity: 0;
            }
            to {
                transform: translateX(0);
                opacity: 1;
            }
        }

        @keyframes fadeOut {
            from {
                transform: translateX(0);
                opacity: 1;
            }
            to {
                transform: translateX(100%);
                opacity: 0;
            }
        }
    `;
    document.head.appendChild(style);
}

// Initialize the alert system when the script loads
initializeAlertSystem();

function showAlert(message, type) {
    // Create the alert container
    const alertBox = document.createElement('div');
    alertBox.className = `alert-box alert-${type}`;

    // Create the icon
    const icon = document.createElement('span');
    icon.className = 'icon';

    // Set icon based on type
    const icons = {
        success: `<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <path d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z" stroke-linecap="round" stroke-linejoin="round"/>
        </svg>`,
        error: `<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <path d="M10 14l2-2m0 0l2-2m-2 2l-2-2m2 2l2 2m7-2a9 9 0 11-18 0 9 9 0 0118 0z" stroke-linecap="round" stroke-linejoin="round"/>
        </svg>`,
        warning: `<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <path d="M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-3L13.732 4c-.77-1.333-2.694-1.333-3.464 0L3.34 16c-.77 1.333.192 3 1.732 3z" stroke-linecap="round" stroke-linejoin="round"/>
        </svg>`,
        info: `<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <path d="M13 16h-1v-4h-1m1-4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z" stroke-linecap="round" stroke-linejoin="round"/>
        </svg>`
    };

    icon.innerHTML = icons[type] || icons.info;

    // Create the message text
    const messageText = document.createElement('span');
    messageText.textContent = message;

    // Create the close button
    const closeButton = document.createElement('button');
    closeButton.className = 'close-btn';
    closeButton.innerHTML = `<svg width="12" height="12" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
        <path d="M6 18L18 6M6 6l12 12" stroke-linecap="round" stroke-linejoin="round"/>
    </svg>`;

    // Assemble the alert
    alertBox.appendChild(icon);
    alertBox.appendChild(messageText);
    alertBox.appendChild(closeButton);

    // Add to document
    document.body.appendChild(alertBox);

    // Handle close button click
    closeButton.addEventListener('click', () => {
        alertBox.style.animation = 'fadeOut 0.3s ease-out forwards';
        setTimeout(() => {
            alertBox.remove();
        }, 300);
    });

    // Auto remove after 3 seconds
    setTimeout(() => {
        if (alertBox.parentElement) {
            alertBox.style.animation = 'fadeOut 0.3s ease-out forwards';
            setTimeout(() => {
                alertBox.remove();
            }, 300);
        }
    }, 3000);
}
