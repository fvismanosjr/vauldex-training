export const formatDateToLong = (dateString: string) => {
    if (!dateString) throw new Error("Date string is required");

    // Create a native Date object from the ISO string
    const date = new Date(dateString);

    if (isNaN(date.getTime())) {
        throw new Error("Invalid date string");
    }

    // Format to a human-readable long format
    return date.toLocaleString('en-US', {
        dateStyle: 'long',
        timeStyle: 'short',
        hour12: true,
    });
}
