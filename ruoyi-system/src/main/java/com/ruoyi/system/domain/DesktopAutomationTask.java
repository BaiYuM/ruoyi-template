package com.ruoyi.system.domain;

import java.util.List;

/**
 * 桌面自动化任务配置实体
 * 
 * @author ruoyi
 * @date 2025-12-26
 */
public class DesktopAutomationTask
{
    /**
     * 任务ID
     */
    private String id;

    /**
     * 任务名称
     */
    private String name;

    /**
     * 任务描述
     */
    private String description;

    /**
     * 是否启用
     */
    private boolean enabled;

    /**
     * 任务配置列表
     */
    private List<TaskConfig> tasks;

    public String getId()
    {
        return id;
    }

    public void setId(String id)
    {
        this.id = id;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getDescription()
    {
        return description;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }

    public boolean isEnabled()
    {
        return enabled;
    }

    public void setEnabled(boolean enabled)
    {
        this.enabled = enabled;
    }

    public List<TaskConfig> getTasks()
    {
        return tasks;
    }

    public void setTasks(List<TaskConfig> tasks)
    {
        this.tasks = tasks;
    }

    /**
     * 任务配置内部类
     */
    public static class TaskConfig
    {
        /**
         * 选择器
         */
        private String selector;

        /**
         * 描述
         */
        private String description;

        /**
         * 动作类型 (click, type, wait, waitForManual, waitForElement等)
         */
        private String action;

        /**
         * 值 (对于type动作)
         */
        private String value;

        /**
         * 是否必需
         */
        private boolean required;

        /**
         * 延迟时间(毫秒)
         */
        private long delay;
        
        /**
         * 等待类型 (elementHasValue, elementExists, urlChanges等)
         */
        private String waitType;
        
        /**
         * 等待元素选择器
         */
        private String waitSelector;
        
        /**
         * 超时时间(毫秒)
         */
        private Long timeout;
        
        /**
         * 提示消息
         */
        private String message;

        public String getSelector()
        {
            return selector;
        }

        public void setSelector(String selector)
        {
            this.selector = selector;
        }

        public String getDescription()
        {
            return description;
        }

        public void setDescription(String description)
        {
            this.description = description;
        }

        public String getAction()
        {
            return action;
        }

        public void setAction(String action)
        {
            this.action = action;
        }

        public String getValue()
        {
            return value;
        }

        public void setValue(String value)
        {
            this.value = value;
        }

        public boolean isRequired()
        {
            return required;
        }

        public void setRequired(boolean required)
        {
            this.required = required;
        }

        public long getDelay()
        {
            return delay;
        }

        public void setDelay(long delay)
        {
            this.delay = delay;
        }
        
        public String getWaitType()
        {
            return waitType;
        }

        public void setWaitType(String waitType)
        {
            this.waitType = waitType;
        }

        public String getWaitSelector()
        {
            return waitSelector;
        }

        public void setWaitSelector(String waitSelector)
        {
            this.waitSelector = waitSelector;
        }

        public Long getTimeout()
        {
            return timeout;
        }

        public void setTimeout(Long timeout)
        {
            this.timeout = timeout;
        }

        public String getMessage()
        {
            return message;
        }

        public void setMessage(String message)
        {
            this.message = message;
        }
    }
}