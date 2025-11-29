package contactmanager.service;

import contactmanager.model.Contact;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class ContactManager {
    private List<Contact> contacts;
    private static final String FILE_NAME = "contacts.ser";

    public ContactManager() {
        this.contacts = new ArrayList<>();
        loadFromFile();
    }

    // Add contact - accepts any Contact type (**Polymorphism**)
    public void addContact(Contact contact) {
        if (contact.getId() == null || contact.getId().isEmpty()) {
            contact.setId(UUID.randomUUID().toString());
        }
        contacts.add(contact);
        saveToFile();
    }

    // Update contact
    public void updateContact(String id, Contact updatedContact) {
        contacts.stream()
            .filter(c -> c.getId().equals(id))
            .findFirst()
            .ifPresent(contact -> {
                contact.setName(updatedContact.getName());
                contact.setPhone(updatedContact.getPhone());
                contact.setEmail(updatedContact.getEmail());
                contact.setAddress(updatedContact.getAddress());
                saveToFile();
            });
    }

    // Delete contact
    public boolean deleteContact(String id) {
        boolean removed = contacts.removeIf(c -> c.getId().equals(id));
        if (removed) {
            saveToFile();
        }
        return removed;
    }

    // Search contacts
    public List<Contact> searchContacts(String query) {
        if (query == null || query.isEmpty()) {
            return getAllContacts();
        }
        return contacts.stream()
            .filter(c -> c.getName().toLowerCase().contains(query.toLowerCase()) ||
                        c.getPhone().contains(query))
            .collect(Collectors.toList());
    }

    public List<Contact> getAllContacts() {
        return new ArrayList<>(contacts);
    }

    // File persistence using Serialization
    private void saveToFile() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_NAME))) {
            oos.writeObject(contacts);
        } catch (IOException e) {
            System.err.println("Error saving contacts: " + e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    private void loadFromFile() {
        File file = new File(FILE_NAME);
        if (!file.exists()) {
            return;
        }
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            contacts = (ArrayList<Contact>) ois.readObject();
        } catch (Exception e) {
            System.err.println("Error loading contacts: " + e.getMessage());
            contacts = new ArrayList<>();
        }
    }

    // Get contact by ID
    public Contact getContactById(String id) {
        return contacts.stream()
            .filter(c -> c.getId().equals(id))
            .findFirst()
            .orElse(null);
    }
}
