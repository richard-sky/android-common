package com.richard.dev.common.activity.speech.model;

import java.io.Serializable;
import java.util.List;

/**
 * @author: Richard
 * @createDate: 2026/6/25 17:46
 * @version: 1.0
 * @description: none
 */
public class SpeechTextResult extends BasicSpeechReply implements Serializable {

    private String speakUrl;
    private Integer ttsSampleRate;
    private String skillId;
    private DmDTO dm;
    private String skill;

    public String getSpeakUrl() {
        return speakUrl;
    }

    public void setSpeakUrl(String speakUrl) {
        this.speakUrl = speakUrl;
    }

    public Integer getTtsSampleRate() {
        return ttsSampleRate;
    }

    public void setTtsSampleRate(Integer ttsSampleRate) {
        this.ttsSampleRate = ttsSampleRate;
    }

    public String getSkillId() {
        return skillId;
    }

    public void setSkillId(String skillId) {
        this.skillId = skillId;
    }

    public DmDTO getDm() {
        return dm;
    }

    public void setDm(DmDTO dm) {
        this.dm = dm;
    }

    public String getSkill() {
        return skill;
    }

    public void setSkill(String skill) {
        this.skill = skill;
    }

    public static class DmDTO {
        private ContextDTO context;
        private WidgetDTO widget;
        private String input;
        private Integer status;
        private String intentName;
        private String task;
        private String taskId;
        private String intentId;
        private String nlg;
        private Boolean shouldEndSession;

        public ContextDTO getContext() {
            return context;
        }

        public void setContext(ContextDTO context) {
            this.context = context;
        }

        public WidgetDTO getWidget() {
            return widget;
        }

        public void setWidget(WidgetDTO widget) {
            this.widget = widget;
        }

        public String getInput() {
            return input;
        }

        public void setInput(String input) {
            this.input = input;
        }

        public Integer getStatus() {
            return status;
        }

        public void setStatus(Integer status) {
            this.status = status;
        }

        public String getIntentName() {
            return intentName;
        }

        public void setIntentName(String intentName) {
            this.intentName = intentName;
        }

        public String getTask() {
            return task;
        }

        public void setTask(String task) {
            this.task = task;
        }

        public String getTaskId() {
            return taskId;
        }

        public void setTaskId(String taskId) {
            this.taskId = taskId;
        }

        public String getIntentId() {
            return intentId;
        }

        public void setIntentId(String intentId) {
            this.intentId = intentId;
        }

        public String getNlg() {
            return nlg;
        }

        public void setNlg(String nlg) {
            this.nlg = nlg;
        }

        public Boolean getShouldEndSession() {
            return shouldEndSession;
        }

        public void setShouldEndSession(Boolean shouldEndSession) {
            this.shouldEndSession = shouldEndSession;
        }

        public static class ContextDTO {
            private String nlgLanguageClass;
            private String currentIntentName;
            private Integer keepListening;
            private Integer keepSession;

            public String getNlgLanguageClass() {
                return nlgLanguageClass;
            }

            public void setNlgLanguageClass(String nlgLanguageClass) {
                this.nlgLanguageClass = nlgLanguageClass;
            }

            public String getCurrentIntentName() {
                return currentIntentName;
            }

            public void setCurrentIntentName(String currentIntentName) {
                this.currentIntentName = currentIntentName;
            }

            public Integer getKeepListening() {
                return keepListening;
            }

            public void setKeepListening(Integer keepListening) {
                this.keepListening = keepListening;
            }

            public Integer getKeepSession() {
                return keepSession;
            }

            public void setKeepSession(Integer keepSession) {
                this.keepSession = keepSession;
            }
        }

        public static class WidgetDTO {
            private ExtraDTO extra;
            private WebhookRespDTO webhookResp;
            private String cityName;
            private String widgetName;
            private String duiWidget;
            private String name;
            private String dmIntent;
            private String type;
            private List<String> recommendations;

            public ExtraDTO getExtra() {
                return extra;
            }

            public void setExtra(ExtraDTO extra) {
                this.extra = extra;
            }

            public WebhookRespDTO getWebhookResp() {
                return webhookResp;
            }

            public void setWebhookResp(WebhookRespDTO webhookResp) {
                this.webhookResp = webhookResp;
            }

            public String getCityName() {
                return cityName;
            }

            public void setCityName(String cityName) {
                this.cityName = cityName;
            }

            public String getWidgetName() {
                return widgetName;
            }

            public void setWidgetName(String widgetName) {
                this.widgetName = widgetName;
            }

            public String getDuiWidget() {
                return duiWidget;
            }

            public void setDuiWidget(String duiWidget) {
                this.duiWidget = duiWidget;
            }

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public String getDmIntent() {
                return dmIntent;
            }

            public void setDmIntent(String dmIntent) {
                this.dmIntent = dmIntent;
            }

            public String getType() {
                return type;
            }

            public void setType(String type) {
                this.type = type;
            }

            public List<String> getRecommendations() {
                return recommendations;
            }

            public void setRecommendations(List<String> recommendations) {
                this.recommendations = recommendations;
            }

            public static class ExtraDTO {
                private String nlgMessage;

                public String getNlgMessage() {
                    return nlgMessage;
                }

                public void setNlgMessage(String nlgMessage) {
                    this.nlgMessage = nlgMessage;
                }
            }

            public static class WebhookRespDTO {
                private BrandDTO brand;
                private String cityName;
                private ExtraDTO extra;
                private Integer errorcode;

                public BrandDTO getBrand() {
                    return brand;
                }

                public void setBrand(BrandDTO brand) {
                    this.brand = brand;
                }

                public String getCityName() {
                    return cityName;
                }

                public void setCityName(String cityName) {
                    this.cityName = cityName;
                }

                public ExtraDTO getExtra() {
                    return extra;
                }

                public void setExtra(ExtraDTO extra) {
                    this.extra = extra;
                }

                public Integer getErrorcode() {
                    return errorcode;
                }

                public void setErrorcode(Integer errorcode) {
                    this.errorcode = errorcode;
                }

                public static class BrandDTO {
                    private String logoMiddle;
                    private String logoLarge;
                    private String isexport;
                    private String showName;
                    private String logoSmall;
                    private String name;

                    public String getLogoMiddle() {
                        return logoMiddle;
                    }

                    public void setLogoMiddle(String logoMiddle) {
                        this.logoMiddle = logoMiddle;
                    }

                    public String getLogoLarge() {
                        return logoLarge;
                    }

                    public void setLogoLarge(String logoLarge) {
                        this.logoLarge = logoLarge;
                    }

                    public String getIsexport() {
                        return isexport;
                    }

                    public void setIsexport(String isexport) {
                        this.isexport = isexport;
                    }

                    public String getShowName() {
                        return showName;
                    }

                    public void setShowName(String showName) {
                        this.showName = showName;
                    }

                    public String getLogoSmall() {
                        return logoSmall;
                    }

                    public void setLogoSmall(String logoSmall) {
                        this.logoSmall = logoSmall;
                    }

                    public String getName() {
                        return name;
                    }

                    public void setName(String name) {
                        this.name = name;
                    }
                }

                public static class ExtraDTO {
                    private Integer sourceId;
                    private IndexDTO index;
                    private List<ForecastDTO> forecast;
                    private List<FutureDTO> future;
                    private ConditionDTO condition;
                    private List<HourForecast24DTO> hourForecast24;

                    public Integer getSourceId() {
                        return sourceId;
                    }

                    public void setSourceId(Integer sourceId) {
                        this.sourceId = sourceId;
                    }

                    public IndexDTO getIndex() {
                        return index;
                    }

                    public void setIndex(IndexDTO index) {
                        this.index = index;
                    }

                    public List<ForecastDTO> getForecast() {
                        return forecast;
                    }

                    public void setForecast(List<ForecastDTO> forecast) {
                        this.forecast = forecast;
                    }

                    public List<FutureDTO> getFuture() {
                        return future;
                    }

                    public void setFuture(List<FutureDTO> future) {
                        this.future = future;
                    }

                    public ConditionDTO getCondition() {
                        return condition;
                    }

                    public void setCondition(ConditionDTO condition) {
                        this.condition = condition;
                    }

                    public List<HourForecast24DTO> getHourForecast24() {
                        return hourForecast24;
                    }

                    public void setHourForecast24(List<HourForecast24DTO> hourForecast24) {
                        this.hourForecast24 = hourForecast24;
                    }

                    public static class IndexDTO {
                        private String humidity;
                        private AqiDTO aqi;
                        private List<LiveIndexDTO> liveIndex;

                        public String getHumidity() {
                            return humidity;
                        }

                        public void setHumidity(String humidity) {
                            this.humidity = humidity;
                        }

                        public AqiDTO getAqi() {
                            return aqi;
                        }

                        public void setAqi(AqiDTO aqi) {
                            this.aqi = aqi;
                        }

                        public List<LiveIndexDTO> getLiveIndex() {
                            return liveIndex;
                        }

                        public void setLiveIndex(List<LiveIndexDTO> liveIndex) {
                            this.liveIndex = liveIndex;
                        }

                        public static class AqiDTO {
                            private String aQIdesc;
                            private String aql;
                            private String pm25;
                            private String aqi;

                            public String getAQIdesc() {
                                return aQIdesc;
                            }

                            public void setAQIdesc(String aQIdesc) {
                                this.aQIdesc = aQIdesc;
                            }

                            public String getAql() {
                                return aql;
                            }

                            public void setAql(String aql) {
                                this.aql = aql;
                            }

                            public String getPm25() {
                                return pm25;
                            }

                            public void setPm25(String pm25) {
                                this.pm25 = pm25;
                            }

                            public String getAqi() {
                                return aqi;
                            }

                            public void setAqi(String aqi) {
                                this.aqi = aqi;
                            }
                        }

                        public static class LiveIndexDTO {
                            private String name;
                            private String desc;
                            private String status;

                            public String getName() {
                                return name;
                            }

                            public void setName(String name) {
                                this.name = name;
                            }

                            public String getDesc() {
                                return desc;
                            }

                            public void setDesc(String desc) {
                                this.desc = desc;
                            }

                            public String getStatus() {
                                return status;
                            }

                            public void setStatus(String status) {
                                this.status = status;
                            }
                        }
                    }

                    public static class ConditionDTO {
                        private String wind;
                        private String weather;
                        private String temperature;
                        private String windLevel;

                        public String getWind() {
                            return wind;
                        }

                        public void setWind(String wind) {
                            this.wind = wind;
                        }

                        public String getWeather() {
                            return weather;
                        }

                        public void setWeather(String weather) {
                            this.weather = weather;
                        }

                        public String getTemperature() {
                            return temperature;
                        }

                        public void setTemperature(String temperature) {
                            this.temperature = temperature;
                        }

                        public String getWindLevel() {
                            return windLevel;
                        }

                        public void setWindLevel(String windLevel) {
                            this.windLevel = windLevel;
                        }
                    }

                    public static class ForecastDTO {
                        private String date;
                        private String weather;
                        private String sunset;
                        private String wind;
                        private String week;
                        private String temperature;
                        private String tempInteval;
                        private String tempTip;
                        private String lowTemp;
                        private String highTemp;
                        private String sunrise;
                        private String tip;
                        private String windLevel;

                        public String getDate() {
                            return date;
                        }

                        public void setDate(String date) {
                            this.date = date;
                        }

                        public String getWeather() {
                            return weather;
                        }

                        public void setWeather(String weather) {
                            this.weather = weather;
                        }

                        public String getSunset() {
                            return sunset;
                        }

                        public void setSunset(String sunset) {
                            this.sunset = sunset;
                        }

                        public String getWind() {
                            return wind;
                        }

                        public void setWind(String wind) {
                            this.wind = wind;
                        }

                        public String getWeek() {
                            return week;
                        }

                        public void setWeek(String week) {
                            this.week = week;
                        }

                        public String getTemperature() {
                            return temperature;
                        }

                        public void setTemperature(String temperature) {
                            this.temperature = temperature;
                        }

                        public String getTempInteval() {
                            return tempInteval;
                        }

                        public void setTempInteval(String tempInteval) {
                            this.tempInteval = tempInteval;
                        }

                        public String getTempTip() {
                            return tempTip;
                        }

                        public void setTempTip(String tempTip) {
                            this.tempTip = tempTip;
                        }

                        public String getLowTemp() {
                            return lowTemp;
                        }

                        public void setLowTemp(String lowTemp) {
                            this.lowTemp = lowTemp;
                        }

                        public String getHighTemp() {
                            return highTemp;
                        }

                        public void setHighTemp(String highTemp) {
                            this.highTemp = highTemp;
                        }

                        public String getSunrise() {
                            return sunrise;
                        }

                        public void setSunrise(String sunrise) {
                            this.sunrise = sunrise;
                        }

                        public String getTip() {
                            return tip;
                        }

                        public void setTip(String tip) {
                            this.tip = tip;
                        }

                        public String getWindLevel() {
                            return windLevel;
                        }

                        public void setWindLevel(String windLevel) {
                            this.windLevel = windLevel;
                        }
                    }

                    public static class FutureDTO {
                        private String date;
                        private String weather;
                        private String wind;
                        private String week;
                        private String temperature;
                        private String windLevel;

                        public String getDate() {
                            return date;
                        }

                        public void setDate(String date) {
                            this.date = date;
                        }

                        public String getWeather() {
                            return weather;
                        }

                        public void setWeather(String weather) {
                            this.weather = weather;
                        }

                        public String getWind() {
                            return wind;
                        }

                        public void setWind(String wind) {
                            this.wind = wind;
                        }

                        public String getWeek() {
                            return week;
                        }

                        public void setWeek(String week) {
                            this.week = week;
                        }

                        public String getTemperature() {
                            return temperature;
                        }

                        public void setTemperature(String temperature) {
                            this.temperature = temperature;
                        }

                        public String getWindLevel() {
                            return windLevel;
                        }

                        public void setWindLevel(String windLevel) {
                            this.windLevel = windLevel;
                        }
                    }

                    public static class HourForecast24DTO {
                        private String weather;
                        private Integer temperature;
                        private String processTime;

                        public String getWeather() {
                            return weather;
                        }

                        public void setWeather(String weather) {
                            this.weather = weather;
                        }

                        public Integer getTemperature() {
                            return temperature;
                        }

                        public void setTemperature(Integer temperature) {
                            this.temperature = temperature;
                        }

                        public String getProcessTime() {
                            return processTime;
                        }

                        public void setProcessTime(String processTime) {
                            this.processTime = processTime;
                        }
                    }
                }
            }
        }
    }
}
