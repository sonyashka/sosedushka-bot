package com.ppobot.bot;

import com.ppobot.controller.RequestController;
import com.ppobot.controller.UserController;
import com.ppobot.entity.ExecutorSkill;
import com.ppobot.entity.Request;
import com.ppobot.entity.RequestForUser;
import com.ppobot.entity.User;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@EqualsAndHashCode(callSuper=false)
@Data
@Service
public class TgBot extends TelegramLongPollingBot {

    private Message message = new Message();
    private final SendMessage response = new SendMessage();
    private final String botUsername;
    private final String botToken;
    private final UserController userController;
    private final RequestController requestController;
    private String curCommand = "";

    public TgBot(@Value("${telegram-bot.name}") String botUsername,
                 @Value("${telegram-bot.token}") String botToken,
                 UserController userController, RequestController requestController) throws TelegramApiException {
        this.botUsername = botUsername;
        this.botToken = botToken;
        this.userController = userController;
        this.requestController = requestController;

        TelegramBotsApi telegramBotsApi = new TelegramBotsApi();
        telegramBotsApi.registerBot(this);
    }

    @Override
    public void onUpdateReceived(Update update) {
        message = update.getMessage();
        response.setChatId(message.getChatId().toString());

        try {
            switch (message.getText()) {
                case "/start":
                    defaultMsg(response, "Добро пожаловать в Соседушку!\nЕсли ты здесь первый раз," +
                            "используй команду /personalize");
                    break;
                case "/personalize":
                    curCommand = "personalize";
                    defaultMsg(response, "Введи свою геопозицию (широту, долготу) и роль (customer, executor, " +
                            "administrator - требует подтверждения).\n" +
                            "Пример:\n54.8284\n42.249\ncustomer"); // если админ - не давать законнектиться
                    break;
                case "/change_role":
                    curCommand = "change_role";
                    defaultMsg(response, "Введи роль, под которой хочешь подключиться: customer, executor, " +
                            "administrator.");
                    break;
                case "/change_geo":
                    curCommand = "change_geo";
                    defaultMsg(response, "Введи новую геопозицию (широту, долготу).\nПример:\n54.8284\n42.249");
                    break;
                case "/add_role":
                    curCommand = "add_role";
                    defaultMsg(response, "Выбери роль для добавления: customer, executor, administrator");
                    break;
                case "/send_to_verify":
                    curCommand = "send_to_verify";
                    defaultMsg(response, "Если ты имеешь роль 'executor', напиши имя навыка, который хочешь " +
                            "отправить на проверку");
                    break;
                case "/get_skill_to_verify":
                    curCommand = "";
                    showNotVerifiedSkill(userController.notVerifiedSkills(message.getFrom()));
                    break;
                case "/skill_verify":
                    curCommand = "skill_verify";
                    defaultMsg(response, "Введи имя пользователя и skillId, которые хочешь подтвердить");
                    break;
                case "/get_admin_to_verify":
                    curCommand = "";
                    showNotVerifiedAdmin(userController.notVerifiedAdmin(message.getFrom()));
                    break;
                case "/admin_verify":
                    curCommand = "admin_verify";
                    defaultMsg(response, "Введи имя пользователя, роль администратора которого хочешь подтвердить");
                    break;
                case "/new_request":
                    curCommand = "new_request";
                    defaultMsg(response, "Введи следующие значения: название (не пустое), срок истечения в формате" +
                            "'гггг-мм-дд чч:мм', пояснение, проф. навык, оборудование. Если какие-то поля не нужны," +
                            "используй пустую строку.\nПример:\nПочинить часы\n2022-06-06 16:00\nНужно заменить батарейку\n" +
                            "\nбатарейка");
                    break;
                case "/my_requests":
                    curCommand = "";
                    myRequests(update);
                    break;
                case "/change_title":
                    curCommand = "change_title";
                    defaultMsg(response, "Введи номер заявки и новое название заявки (непустое)\nПример:\n" +
                            "3\nОткрыть банку");
                    break;
                case "/change_time":
                    curCommand = "change_time";
                    defaultMsg(response, "Вееди новый срок истечения в формате 'гггг-мм-дд чч:мм'");
                    break;
                case "/change_prof":
                    curCommand = "change_prof";
                    defaultMsg(response, "Введи новый необходимый проф. навык");
                    break;
                case "/change_explanation":
                    curCommand = "change_explanation";
                    defaultMsg(response, "Введи новое пояснение");
                    break;
                case "/change_equipment":
                    curCommand = "change_equipment";
                    defaultMsg(response, "Введи новое необходимое оборудование");
                    break;
                case "/get_request_on_my_skill":
                    curCommand = "get_request_on_my_skill";
                    defaultMsg(response, "Введи расстояние, в котором ищешь заявки (в км)");
                    break;
                case "/take":
                    curCommand = "take";
                    defaultMsg(response, "Введи номер заявки, которую хочешь взять на исполнение");
                    break;
                case "/done":
                    curCommand = "done";
                    defaultMsg(response, "Введи номер заявки, которую хочешь закрыть по исполнению");
                    break;
                case "/close":
                    curCommand = "close";
                    defaultMsg(response, "Введи номер заявки для закрытия");
                    break;
                case "/help":
                    curCommand = "";
                    help();
                    break;
                default:
                    switch (curCommand) {
                        case "personalize":
                            personalize(update);
                            break;
                        case "change_role":
                            changeRole(update);
                            break;
                        case "change_geo":
                            changeGeo(update);
                            break;
                        case "add_role":
                            addRole(update);
                            break;
                        case "send_to_verify":
                            sendToVerify(update);
                            break;
                        case "skill_verify":
                            skillVerify(update);
                            break;
                        case "admin_verify":
                            adminVerify(update);
                            break;
                        case "new_request":
                            newRequest(update);
                            break;
                        case "change_title":
                            changeTitle(update);
                            break;
                        case "change_time":
                            changeTime(update);
                            break;
                        case "change_prof":
                            changeProf(update);
                            break;
                        case "change_explanation":
                            changeExplanation(update);
                            break;
                        case "change_equipment":
                            changeEquipment(update);
                            break;
                        case "get_request_on_my_skill":
                            getRequestOnMySkill(update); // страшная вещь, нужно подправить
                            break;
                        case "take":
                            take(update);
                            break;
                        case "done":
                            done(update);
                            break;
                        case "close":
                            close(update);
                            break;
                        default:
                            defaultMsg(response, "Ну вот, все сбилось. Введи команду заново");
                            break;
                    }
                    curCommand = "";
                    break;
            }
        } catch (TelegramApiException tgException) {
            tgException.getStackTrace();
        }
    }

    private void myRequests(Update update) throws TelegramApiException {
        switch (userController.getConnection(message.getFrom())) {
            case 1:
                showMyRequests(requestController.requestsByOwner(message.getFrom()), "executor");
                break;
            case 2:
                showMyRequests(requestController.requestsByExecutor(message.getFrom()), "owner");
                break;
            default:
                defaultMsg(response, "Нет возможности посмотреть заявки:)");
                break;
        }
    }

    private void showMyRequests(List<RequestForUser> reqs, String show) throws TelegramApiException {
        if (reqs == null) {
            defaultMsg(response, "У вас нет заявок");
        } else {
            String res = "Список ваших заявок: найдено " + Integer.toString(reqs.size()) + "\n";
            for (RequestForUser req : reqs) {
                res += Integer.toString(req.getId()) + ". " + req.getTitle() + "\nВыполнить до: " +
                        new SimpleDateFormat("yyyy-MM-dd HH:mm").format(new Date(req.getPeriodOfRelevance().getTime())) +
                        "\nПояснение: " + req.getExplanation() + "\nПроф. навык: " + req.getProfNecessity() +
                        "\nОборудование: " + req.getEquipment() + "\n";
                switch (show) {
                    case "owner":
                        res += "Оформитель: ";
                        break;
                    case "executor":
                        res += "Исполнитель: ";
                        break;
                }
                res += req.getUser() + "\nСтатус: " + req.getStatus() + "\n";
            }
            defaultMsg(response, res);
        }
    }

    private void showNotVerifiedSkill(List<ExecutorSkill> exSkills) throws TelegramApiException {
        if (exSkills == null) {
            defaultMsg(response, "Недостаточно прав или нет подключения под ролью. Воспользуйтесь /help");
        } else {
            String res = "Список неподтвержденных навыков: найдено " + Integer.toString(exSkills.size()) + "\n";
            for (ExecutorSkill exSkill : exSkills) {
                res += exSkill.getExecutorName() + "\nskillId: " +
                        Integer.toString(exSkill.getSkillId()) + "\nskill: " +
                        userController.skillNameOnId(exSkill.getSkillId()) + "\n";
            }
            defaultMsg(response, res);
        }
    }

    private void showNotVerifiedAdmin(List<User> users) throws TelegramApiException {
        if (users == null) {
            defaultMsg(response, "Недостаточно прав или нет подключения под ролью. Воспользуйтесь /help");
        } else {
            String res = "Список неподтвержденных админов: найдено " + Integer.toString(users.size()) + "\n";
            for (User user : users) {
                res += user.getTgName() + "\n";
            }
            defaultMsg(response, res);
        }
    }

    private void showMySkillRequests(List<RequestForUser> requests) throws TelegramApiException {
        if (requests == null) {
            defaultMsg(response, "Под ваши навыки нет ни одной заявки");
        } else {
            String res = "Заявки, подходящие под ваши навыки (подтвержденные): найдено " +
                    Integer.toString(requests.size()) + "\n";
            for (RequestForUser req : requests) {
                res += Integer.toString(req.getId()) + ". " + req.getTitle() + "\nВыполнить до: " +
                        new SimpleDateFormat("yyyy-MM-dd HH:mm").format(new Date(req.getPeriodOfRelevance().getTime())) +
                        "\nПояснение: " + req.getExplanation() + "\nПроф. навык: " + req.getProfNecessity() +
                        "\nОборудование: " + req.getEquipment() + "\nОформитель: " + req.getUser() + "\nСтатус: " + req.getStatus() + "\n";
            }
            defaultMsg(response, res);
        }
    }

    private void personalize(Update update) throws TelegramApiException {
        String[] params = update.getMessage().getText().split("\n");
        if (params.length < 3) {
            defaultMsg(response, "Недостаточно параметров. Введи команду заново.");
        } else {
            String geoLat = params[0];
            String geoLong = params[1];
            String role = params[2];
            int res = userController.personalizeUser(message.getFrom(), geoLat, geoLong, role);
            switch (res) {
                case HttpStatus.SC_METHOD_FAILURE:
                    defaultMsg(response, "Какие-то данные введены некорректно");
                    break;
                case HttpStatus.SC_METHOD_NOT_ALLOWED:
                    defaultMsg(response, "Роль администратора подтверждается не сразу. Вы можете добавить себе " +
                            "другую роль командой /add_role");
                    break;
                case HttpStatus.SC_NOT_FOUND:
                    defaultMsg(response, "Вы уже прошли персонализацию. Используйте /help, чтобы получить список " +
                            "доступных команд");
                    break;
                default:
                    defaultMsg(response, "Персонализация прошла успешно!\nДля просмотра списка команд по " +
                            "текущей роли набери /help");
                    break;
            }
        }
    }

    public void changeRole(Update update) throws TelegramApiException {
        String[] params = update.getMessage().getText().split("\n");
        if (params.length < 1) {
            defaultMsg(response, "Недостаточно параметров. Введи команду заново.");
        } else {
            String role = params[0];
            int res = userController.changeRole(message.getFrom(), role);
            switch (res) {
                case HttpStatus.SC_NOT_FOUND:
                    defaultMsg(response, "Вы еще не прошли персонализацию. Воспользуйтесь командой /personalize");
                    break;
                case HttpStatus.SC_BAD_GATEWAY:
                    defaultMsg(response, "Вы еще не подключились под ролью. Воспользуйтесь командой /add_role");
                    break;
                case HttpStatus.SC_METHOD_NOT_ALLOWED:
                    defaultMsg(response, "Вы еще не обладаете ролью, на которую хотите переключиться");
                    break;
                case HttpStatus.SC_METHOD_FAILURE:
                    defaultMsg(response, "Выбраной роли не существует");
                    break;
                default:
                    defaultMsg(response, "Смена роли прошла успешно. Воспользуйтесь /help для просмотра " +
                            "доступных команд");
                    break;
            }
        }
    }

    private void changeGeo(Update update) throws TelegramApiException {
        String[] params = update.getMessage().getText().split("\n");
        if (params.length < 2) {
            defaultMsg(response, "Недостаточно параметров. Введи команду заново.");
        } else {
            String geoLat = params[0];
            String geoLong = params[1];
            int res = userController.changeGeo(message.getFrom(), geoLat, geoLong);
            switch (res) {
                case HttpStatus.SC_NOT_FOUND:
                    defaultMsg(response, "Вы еще не прошли персонализацию");
                    break;
                case HttpStatus.SC_METHOD_FAILURE:
                    defaultMsg(response, "Какие-то данные введены некорректно");
                    break;
                default:
                    defaultMsg(response, "Смена геолокации прошла успешно!");
                    break;
            }
        }
    }

    private void addRole(Update update) throws TelegramApiException {
        String[] params = update.getMessage().getText().split("\n");
        if (params.length < 1) {
            defaultMsg(response, "Недостаточно параметров. Введи команду заново.");
        } else {
            String role = params[0];
            int res = userController.addRole(message.getFrom(), role);
            switch (res) {
                case HttpStatus.SC_NOT_FOUND:
                    defaultMsg(response, "Вы еще не прошли персонализацию");
                    break;
                case HttpStatus.SC_METHOD_FAILURE:
                    defaultMsg(response, "Выбраной роли не существует");
                    break;
                case HttpStatus.SC_METHOD_NOT_ALLOWED:
                    defaultMsg(response, "Роль администратора подтверждается не сразу. Нужно подождать");
                    break;
                default:
                    defaultMsg(response, "Добавление роли прошло успешно!");
                    break;
            }
        }
    }

    private void sendToVerify(Update update) throws TelegramApiException {
        String[] params = update.getMessage().getText().split("\n");
        if (params.length < 1) {
            defaultMsg(response, "Недостаточно параметров. Введи команду заново.");
        } else {
            String skill = params[0];
            int res = userController.sendOnVerification(message.getFrom(), skill);
            switch (res) {
                case HttpStatus.SC_NOT_FOUND:
                    defaultMsg(response, "Вы еще не прошли персонализацию");
                    break;
                case HttpStatus.SC_METHOD_NOT_ALLOWED:
                    defaultMsg(response, "У вас нет роли исполнителя (executor) для этого действия " +
                            "или вы подключены под другой ролью");
                    break;
                default:
                    defaultMsg(response, "Отправка навыка на проверку прошло успешно!");
                    break;
            }
        }
    }

    private void skillVerify(Update update) throws TelegramApiException {
        String[] params = update.getMessage().getText().split("\n");
        if (params.length < 2) {
            defaultMsg(response, "Недостаточно параметров. Введи команду заново.");
        } else {
            String tgName = params[0];
            String skillId = params[1];
            int res = userController.skillVerify(message.getFrom(), tgName, skillId);
            switch (res) {
                case HttpStatus.SC_METHOD_NOT_ALLOWED:
                    defaultMsg(response, "У вас нет роли администратора (administrator) для этого действия");
                    break;
                case HttpStatus.SC_METHOD_FAILURE:
                    defaultMsg(response, "Введены некорректные данные");
                    break;
                default:
                    defaultMsg(response, "Подтверждение навыка прошло успешно!");
                    break;
            }
        }
    }

    private void adminVerify(Update update) throws TelegramApiException {
        String[] params = update.getMessage().getText().split("\n");
        if (params.length < 1) {
            defaultMsg(response, "Недостаточно параметров. Введи команду заново.");
        } else {
            String tgName = params[0];
            int res = userController.adminVerify(message.getFrom(), tgName);
            switch (res) {
                case HttpStatus.SC_METHOD_NOT_ALLOWED:
                    defaultMsg(response, "У вас нет роли администратора (administrator) для этого действия");
                    break;
                case HttpStatus.SC_METHOD_FAILURE:
                    defaultMsg(response, "Введены некорректные данные");
                    break;
                default:
                    defaultMsg(response, "Подтверждение администратора прошло успешно!");
                    break;
            }
        }
    }

    private void newRequest(Update update) throws TelegramApiException {
        String[] params = update.getMessage().getText().split("\n");
        if (params.length < 5) {
            defaultMsg(response, "Недостаточно параметров. Введи команду заново.");
        } else {
            String title = params[0];
            String period = params[1];
            String explanation = params[2];
            String prof = params[3];
            String equipment = params[4];
            int res;
            if (userController.getConnection(message.getFrom()) == 1) {
                res = requestController.createRequest(message.getFrom(), title, period, explanation, prof, equipment);
            } else {
                res = HttpStatus.SC_METHOD_NOT_ALLOWED;
            }
            switch (res) {
                case HttpStatus.SC_METHOD_FAILURE:
                    defaultMsg(response, "Какие-то данные введены некорректно или текущее время больше указанного");
                    break;
                case HttpStatus.SC_METHOD_NOT_ALLOWED:
                    defaultMsg(response, "Вы не обладаете правами на эту заявку или текущееподключение не " +
                            "соответствует роли customer");
                    break;
                default:
                    defaultMsg(response, "Заяввка создана успешно!");
                    break;
            }
        }
    }

    private void changeTitle(Update update) throws TelegramApiException {
        String[] params = update.getMessage().getText().split("\n");
        if (params.length < 2) {
            defaultMsg(response, "Недостаточно параметров. Введи команду заново.");
        } else {
            String reqId = params[0];
            String title = params[1];
            int res;
            if (userController.getConnection(message.getFrom()) == 1) {
                res = requestController.changeTitle(message.getFrom(), reqId, title);
            } else {
                res = HttpStatus.SC_METHOD_NOT_ALLOWED;
            }
            switch (res) {
                case HttpStatus.SC_NOT_FOUND:
                    defaultMsg(response, "У вас нет такой заявки");
                    break;
                case HttpStatus.SC_METHOD_FAILURE:
                    defaultMsg(response, "Введен некорректный номер заявки");
                    break;
                case HttpStatus.SC_METHOD_NOT_ALLOWED:
                    defaultMsg(response, "Вы не обладаете правами на эту заявку или текущееподключение не " +
                            "соответствует роли customer");
                    break;
                default:
                    defaultMsg(response, "Название заявки изменено успешно!");
                    break;
            }
        }
    }

     private void changeTime(Update update) throws TelegramApiException {
         String[] params = update.getMessage().getText().split("\n");
         if (params.length < 2) {
             defaultMsg(response, "Недостаточно параметров. Введи команду заново.");
         } else {
             String reqId = params[0];
             String time = params[1];
             int res;
             if (userController.getConnection(message.getFrom()) == 1) {
                 res = requestController.changePeriodOfRelevance(message.getFrom(), reqId, time);
             } else {
                 res = HttpStatus.SC_METHOD_NOT_ALLOWED;
             }
             switch (res) {
                 case HttpStatus.SC_NOT_FOUND:
                     defaultMsg(response, "У вас нет такой заявки");
                     break;
                 case HttpStatus.SC_METHOD_FAILURE:
                     defaultMsg(response, "Введен некорректный номер заявки или неверный формат времени (а может " +
                             "сейчас времени больше, чем введено)");
                     break;
                 case HttpStatus.SC_METHOD_NOT_ALLOWED:
                     defaultMsg(response, "Вы не обладаете правами на эту заявку или текущееподключение не " +
                             "соответствует роли customer");
                     break;
                 default:
                     defaultMsg(response, "Время изменено успешно!");
                     break;
             }
         }
     }

    private void changeProf(Update update) throws TelegramApiException {
        String[] params = update.getMessage().getText().split("\n");
        if (params.length < 2) {
            defaultMsg(response, "Недостаточно параметров. Введи команду заново.");
        } else {
            String reqId = params[0];
            String prof = params[1];
            int res;
            if (userController.getConnection(message.getFrom()) == 1) {
                res = requestController.changeProfNecessity(message.getFrom(), reqId, prof);
            } else {
                res = HttpStatus.SC_METHOD_NOT_ALLOWED;
            }
            switch (res) {
                case HttpStatus.SC_NOT_FOUND:
                    defaultMsg(response, "У вас нет такой заявки");
                    break;
                case HttpStatus.SC_METHOD_FAILURE:
                    defaultMsg(response, "Введен некорректный номер заявки");
                    break;
                case HttpStatus.SC_METHOD_NOT_ALLOWED:
                    defaultMsg(response, "Вы не обладаете правами на эту заявку или текущееподключение не " +
                            "соответствует роли customer");
                    break;
                case HttpStatus.SC_CONFLICT:
                    defaultMsg(response, "Нельзя менять проф. навык заявки вне статуса OPENED");
                    break;
                default:
                    defaultMsg(response, "Необходый проф. навык изменен успешно!");
                    break;
            }
        }
    }

    private void changeExplanation(Update update) throws TelegramApiException {
        String[] params = update.getMessage().getText().split("\n");
        if (params.length < 2) {
            defaultMsg(response, "Недостаточно параметров. Введи команду заново.");
        } else {
            String reqId = params[0];
            String explanation = params[1];
            int res;
            if (userController.getConnection(message.getFrom()) == 1) {
                res = requestController.changeExplanation(message.getFrom(), reqId, explanation);
            } else {
                res = HttpStatus.SC_METHOD_NOT_ALLOWED;
            }
            switch (res) {
                case HttpStatus.SC_NOT_FOUND:
                    defaultMsg(response, "У вас нет такой заявки");
                    break;
                case HttpStatus.SC_METHOD_FAILURE:
                    defaultMsg(response, "Введен некорректный номер заявки");
                    break;
                case HttpStatus.SC_METHOD_NOT_ALLOWED:
                    defaultMsg(response, "Вы не обладаете правами на эту заявку или текущееподключение не " +
                            "соответствует роли customer");
                    break;
                default:
                    defaultMsg(response, "Пояснени изменено успешно!");
                    break;
            }
        }
    }

    private void changeEquipment(Update update) throws TelegramApiException {
        String[] params = update.getMessage().getText().split("\n");
        if (params.length < 2) {
            defaultMsg(response, "Недостаточно параметров. Введи команду заново.");
        } else {
            String reqId = params[0];
            String equipment = params[1];
            int res;
            if (userController.getConnection(message.getFrom()) == 1) {
                res = requestController.changeEquipment(message.getFrom(), reqId, equipment);
            } else {
                res = HttpStatus.SC_METHOD_NOT_ALLOWED;
            }
            switch (res) {
                case HttpStatus.SC_NOT_FOUND:
                    defaultMsg(response, "У вас нет такой заявки");
                    break;
                case HttpStatus.SC_METHOD_FAILURE:
                    defaultMsg(response, "Введен некорректный номер заявки");
                    break;
                case HttpStatus.SC_METHOD_NOT_ALLOWED:
                    defaultMsg(response, "Вы не обладаете правами на эту заявку или текущееподключение не " +
                            "соответствует роли customer");
                    break;
                default:
                    defaultMsg(response, "Оборудование изменено успешно!");
                    break;
            }
        }
    }

    private void getRequestOnMySkill(Update update) throws TelegramApiException {
        String[] params = update.getMessage().getText().split("\n");
        if (params.length < 1) {
            defaultMsg(response, "Недостаточно параметров. Введи команду заново.");
        } else {
            String distance = params[0];
            if (userController.getConnection(message.getFrom()) == 2) {
                showMySkillRequests(requestController.requestsByExecutorSkills(message.getFrom(), distance));
            } else {
                defaultMsg(response, "Ваше подключение не соотвутствует роли исполнителя");
            }
        }
    }

    private void take(Update update) throws TelegramApiException {
        String[] params = update.getMessage().getText().split("\n");
        String reqId = params[0];
        int res = requestController.takeOnExecution(message.getFrom(), reqId);
        if (userController.getConnection(message.getFrom()) != 2) {
            res = HttpStatus.SC_METHOD_NOT_ALLOWED;
        }
        switch (res) {
            case HttpStatus.SC_NOT_FOUND:
                defaultMsg(response, "Такой заявки не найдено или она закрыта");
                break;
            case HttpStatus.SC_METHOD_FAILURE:
                defaultMsg(response, "Введен некорректный номер заявки");
                break;
            case HttpStatus.SC_METHOD_NOT_ALLOWED:
                defaultMsg(response, "У вас недостаточно прав для выполнения заявок");
                break;
            case HttpStatus.SC_CONFLICT:
                defaultMsg(response, "Отсутствует необходимый навык");
                break;
            default:
                defaultMsg(response, "Взято на исполнение успешно!");
                break;
        }
    }

    private void done(Update update) throws TelegramApiException {
        String[] params = update.getMessage().getText().split("\n");
        String reqId = params[0];
        int res = requestController.done(message.getFrom(), reqId);
        if (userController.getConnection(message.getFrom()) != 2) {
            res = HttpStatus.SC_METHOD_NOT_ALLOWED;
        }
        switch (res) {
            case HttpStatus.SC_NOT_FOUND:
                defaultMsg(response, "Такой заявки на вас не найдено или она закрыта");
                break;
            case HttpStatus.SC_METHOD_FAILURE:
                defaultMsg(response, "Введен некорректный номер заявки");
                break;
            case HttpStatus.SC_METHOD_NOT_ALLOWED:
                defaultMsg(response, "У вас недостаточно прав для выполнения заявок");
                break;
            default:
                defaultMsg(response, "Заявка выполнена успешно!");
                break;
        }
    }

    private void close(Update update) throws TelegramApiException {
        String[] params = update.getMessage().getText().split("\n");
        String reqId = params[0];
        int res;
        if (userController.getConnection(message.getFrom()) == 1) {
            res = requestController.closeRequest(message.getFrom(), reqId);
        } else {
            res = HttpStatus.SC_METHOD_NOT_ALLOWED;
        }
        switch (res) {
            case HttpStatus.SC_NOT_FOUND:
                defaultMsg(response, "У вас нет такой заявки или вы не являлись ее оформителем или заявка не " +
                        "подлежит закрытию (т.е. не находится в статусе OPENED)");
                break;
            case HttpStatus.SC_METHOD_FAILURE:
                defaultMsg(response, "Введен некорректный номер заявки");
                break;
            case HttpStatus.SC_METHOD_NOT_ALLOWED:
                defaultMsg(response, "Вы не обладаете правами на эту заявку или текущееподключение не " +
                        "соответствует роли customer");
                break;
            default:
                defaultMsg(response, "Заявка закрыта успешно!");
                break;
        }
    }

    private void help() throws TelegramApiException {
        int res = userController.getConnection(message.getFrom());
        switch (res) {
            case 1:
                defaultMsg(response, "Доступные оформителю команды:\n/change_geo\n/add_role\n/change_role\n/new_request" +
                        "\n/my_requests" +
                        "\n/change_title\n/change_time\n/change_prof\n/change_equipment\n/change_explanation" +
                        "\n/close");
                break;
            case 2:
                defaultMsg(response, "Доступные исполнителю команды:\n/change_geo\n/add_role\n/change_role" +
                        "\n/send_to_verify\n" +
                        "/get_request_on_my_skill\n/take\n/my_requests\n/done");
                break;
            case 3:
                defaultMsg(response, "Доступные администратору команды:\n/change_geo\n/add_role\n/change_role" +
                        "\n/get_skill_to_verify\n/skill_verify" +
                        "\n/get_admin_to_verify\n/admin_verify");
                break;
            case HttpStatus.SC_NOT_FOUND:
                defaultMsg(response, "Вы еще не прошли персонализацию\n" +
                        "Воспользуйтесь командой /personalize, если еще не прошли персонализацию");
                break;
            case HttpStatus.SC_BAD_GATEWAY:
                defaultMsg(response, "Вы еще не подключились под определенной ролью\n" +
                        "Воспользуйтесь командой /add_role для добавления роли и соответствующего подключения");
                break;
            default:
                defaultMsg(response, "Упс, что-то пошло не так");
                break;
        }
    }

    private void defaultMsg(SendMessage response, String msg) throws TelegramApiException {
        response.setText(msg);
        execute(response);
    }
}
