package ex12;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

public class DigitalCalendar {
    private JFrame frame;
    private JPanel calendarPanel;
    private JTextArea scheduleDisplay;
    private JLabel monthLabel;
    private LocalDate currentMonth;
    private LocalDate selectedDate; 
    private HashMap<LocalDate, ArrayList<String>> scheduleData;
    private JButton editButton; 

    public DigitalCalendar() {
        frame = new JFrame("デジタルカレンダー");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);

        scheduleData = new HashMap<>();
        currentMonth = LocalDate.now().withDayOfMonth(1); 

        calendarPanel = new JPanel(new GridLayout(0, 7)); 
        initializeCalendar();

        JPanel navigationPanel = new JPanel(new BorderLayout());
        JButton prevButton = new JButton("前月");
        JButton nextButton = new JButton("次月");
        monthLabel = new JLabel("", SwingConstants.CENTER);
        updateMonthLabel();

        prevButton.addActionListener(e -> changeMonth(-1));
        nextButton.addActionListener(e -> changeMonth(1));

        navigationPanel.add(prevButton, BorderLayout.WEST);
        navigationPanel.add(monthLabel, BorderLayout.CENTER);
        navigationPanel.add(nextButton, BorderLayout.EAST);

        scheduleDisplay = new JTextArea();
        scheduleDisplay.setEditable(false);
        scheduleDisplay.setLineWrap(true);
        scheduleDisplay.setWrapStyleWord(true);
        JScrollPane scheduleScrollPane = new JScrollPane(scheduleDisplay);

        frame.setLayout(new BorderLayout());
        frame.add(navigationPanel, BorderLayout.NORTH);
        frame.add(calendarPanel, BorderLayout.CENTER);
        frame.add(scheduleScrollPane, BorderLayout.EAST);

        editButton = new JButton("予定を追加");
        editButton.addActionListener(e -> {
            if (selectedDate != null) {
                openScheduleDialog(selectedDate);
            }
        });
        editButton.setEnabled(false); 
        frame.add(editButton, BorderLayout.SOUTH);

        frame.setVisible(true);
    }

    private void initializeCalendar() {
        calendarPanel.removeAll(); 

        String[] daysOfWeek = {"Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"};
        for (int i = 0; i < daysOfWeek.length; i++) {
            JLabel label = new JLabel(daysOfWeek[i], SwingConstants.CENTER);
            if (i == 0) {
                label.setForeground(Color.RED); 
            } else if (i == 6) {
                label.setForeground(Color.BLUE); 
            }
            calendarPanel.add(label);
        }

        YearMonth yearMonth = YearMonth.from(currentMonth);
        int daysInMonth = yearMonth.lengthOfMonth();
        int startDayOfWeek = currentMonth.getDayOfWeek().getValue() % 7; 

        for (int i = 0; i < startDayOfWeek; i++) {
            calendarPanel.add(new JLabel(""));
        }

        for (int day = 1; day <= daysInMonth; day++) {
            LocalDate date = currentMonth.withDayOfMonth(day);
            JButton dayButton = new JButton(String.valueOf(day));
            dayButton.setFont(new Font("Arial", Font.PLAIN, 12));

            if (date.getDayOfWeek().getValue() == 6) { 
                dayButton.setForeground(Color.BLUE);
            } else if (date.getDayOfWeek().getValue() == 7) { 
                dayButton.setForeground(Color.RED);
            }

            dayButton.addActionListener(e -> {
                selectedDate = date; 
                updateScheduleDisplay(date);
                editButton.setEnabled(true); 
            });

            calendarPanel.add(dayButton);
        }

        int totalCells = startDayOfWeek + daysInMonth;
        while (totalCells % 7 != 0) {
            calendarPanel.add(new JLabel(""));
            totalCells++;
        }

        calendarPanel.revalidate();
        calendarPanel.repaint();
    }

    private void openScheduleDialog(LocalDate date) {
        JDialog dialog = new JDialog(frame, "スケジュール: " + date, true);
        dialog.setSize(400, 300);
        dialog.setLayout(new BorderLayout());

        JTextArea scheduleInput = new JTextArea(5, 20);
        scheduleInput.setLineWrap(true);
        scheduleInput.setWrapStyleWord(true);

        JButton saveButton = new JButton("保存");
        saveButton.addActionListener(e -> {
            saveSchedule(date, scheduleInput.getText());
            dialog.dispose();
        });

        dialog.add(new JLabel("予定を入力:"), BorderLayout.NORTH);
        dialog.add(new JScrollPane(scheduleInput), BorderLayout.CENTER);
        dialog.add(saveButton, BorderLayout.SOUTH);

        dialog.setVisible(true);
    }

    private void saveSchedule(LocalDate date, String schedule) {
        if (schedule == null || schedule.isBlank()) {
            return;
        }

        scheduleData.putIfAbsent(date, new ArrayList<>());
        scheduleData.get(date).add(schedule);

        updateScheduleDisplay(date);

        System.out.println("Saved schedule for " + date + ": " + schedule);
    }

    private void updateScheduleDisplay(LocalDate date) {
        ArrayList<String> schedules = scheduleData.getOrDefault(date, new ArrayList<>());
        StringBuilder displayText = new StringBuilder("【 " + date + " のスケジュール 】\n");
        for (int i = 0; i < schedules.size(); i++) {
            displayText.append((i + 1)).append(". ").append(schedules.get(i)).append("\n");
        }
        scheduleDisplay.setText(displayText.toString());

        System.out.println("Updated schedule display for " + date + ": " + displayText.toString());
    }

    private void changeMonth(int offset) {
        currentMonth = currentMonth.plusMonths(offset);
        updateMonthLabel();
        initializeCalendar();
    }

    private void updateMonthLabel() {
        monthLabel.setText(currentMonth.getYear() + "年 " + currentMonth.getMonthValue() + "月");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(DigitalCalendar::new);
    }
}
