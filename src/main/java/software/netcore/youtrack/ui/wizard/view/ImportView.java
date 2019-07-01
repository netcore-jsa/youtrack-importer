package software.netcore.youtrack.ui.wizard.view;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Hr;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.progressbar.ProgressBar;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;
import org.springframework.util.concurrent.ListenableFuture;
import software.netcore.youtrack.buisness.client.entity.Issue;
import software.netcore.youtrack.buisness.client.entity.IssueComment;
import software.netcore.youtrack.buisness.client.entity.bundle.element.BundleElement;
import software.netcore.youtrack.buisness.client.entity.field.issue.IssueCustomField;
import software.netcore.youtrack.buisness.client.entity.field.issue.SingleUserIssueCustomField;
import software.netcore.youtrack.buisness.client.entity.field.issue.base.BaseIssueCustomField;
import software.netcore.youtrack.buisness.service.youtrack.YouTrackService;
import software.netcore.youtrack.buisness.service.youtrack.entity.TranslatedIssues;
import software.netcore.youtrack.ui.wizard.conf.WizardFlow;
import software.netcore.youtrack.ui.wizard.conf.YouTrackImporterStorage;

import java.util.*;

/**
 * @since v. 1.0.0
 */
@Slf4j
@PageTitle("YouTrack importer")
@Route(value = ImportView.NAVIGATION, layout = WizardFlowView.class)
public class ImportView extends AbstractFlowStepView<YouTrackImporterStorage, TranslatedIssues> {

    public static final String NAVIGATION = "import_finalizing";
    private static final int PAGE_SIZE = 5;

    private final YouTrackService service;

    private VerticalLayout contentContainer = new VerticalLayout();
    private TranslatedIssues translatedIssues;

    ImportView(YouTrackImporterStorage storage, WizardFlow wizardFlow, YouTrackService service) {
        super(storage, wizardFlow);
        this.service = service;
    }

    @Override
    public String getNavigation() {
        return NAVIGATION;
    }

    @Override
    public boolean isValid() {
        return false;
    }

    @Override
    void buildView() {
        removeAll();
        setWidthFull();
        setMargin(false);
        setPadding(false);

        contentContainer.setWidthFull();
        contentContainer.setMargin(false);

        add(new H3("Review & issues import"));
        add(contentContainer);

        if (hasStoredConfig()) {
            showTranslatedIssues();
        } else {
            translateCsvToYouTrackIssues();
        }
    }

    private void doImport() {
        contentContainer.removeAll();

        contentContainer.add("Importing issues");
        ProgressBar progressBar = new ProgressBar();
        progressBar.setIndeterminate(true);
        contentContainer.add(progressBar);

        ListenableFuture<Void> future = service.importTranslatedIssues(getStorage()
                .getConnectionConfig(), translatedIssues);
        future.addCallback(result -> getUI().ifPresent(ui -> ui.access(() -> {
            contentContainer.removeAll();
            contentContainer.add(new Label("Issues have been imported"));
        })), ex -> {
            contentContainer.removeAll();
            contentContainer.add(new Label("Failed to import issues"));
            contentContainer.add(new Label("Reason = '" + ex.getMessage() + ""));
            contentContainer.add(new Button("Retry", event -> doImport()));
        });
    }

    private void translateCsvToYouTrackIssues() {
        contentContainer.removeAll();

        contentContainer.add("Translating issues");
        ProgressBar progressBar = new ProgressBar();
        progressBar.setIndeterminate(true);
        contentContainer.add(progressBar);

        ListenableFuture<TranslatedIssues> future = service.translateIssuesMapping(
                getStorage().getConnectionConfig(), getStorage().getCsvReadResult(),
                getStorage().getCustomFieldsConfig(), getStorage().getMandatoryFieldsMapping(),
                getStorage().getUsersMapping(), getStorage().getEnumsConfig());
        future.addCallback(result -> getUI().ifPresent(ui -> ui.access(() -> {
                    translatedIssues = result;
                    showTranslatedIssues();
                }))
                , ex -> getUI().ifPresent(ui -> ui.access(() -> showTranslationError(ex))));
    }

    private void showTranslationError(Throwable ex) {
        contentContainer.removeAll();
        contentContainer.add(new Label("Failed to translate CSV issues to YouTrack issues"));
        contentContainer.add(new Label("Reason = " + (
                ex.getMessage() == null ? ex.getClass().getName() : ex.getMessage())));
        contentContainer.add(new Button("Retry", event -> translateCsvToYouTrackIssues()));
    }

    private void showTranslatedIssues() {
        contentContainer.removeAll();
        contentContainer.add(new Button("Import", event -> doImport()));

        Page page = new Page(translatedIssues.getIssues().size(), PAGE_SIZE);
        List<IssueLayout> issueLayouts = new ArrayList<>();
        for (Issue issue : translatedIssues.getIssues()) {
            IssueLayout issueLayout = new IssueLayout(issue, translatedIssues.getIssueComments().get(issue));
            issueLayouts.add(issueLayout);
        }

        Label pagingLabel = new Label();
        pagingLabel.getElement().getStyle().set("text-align", "center");
        pagingLabel.setWidth("100px");
        VerticalLayout issuesContainer = new VerticalLayout();
        add(issuesContainer);

        Button next = new Button(VaadinIcon.ARROW_RIGHT.create());
        Button previous = new Button(VaadinIcon.ARROW_LEFT.create());

        next.addClickListener(event -> {
            boolean paged = page.incrementPage();
            if (paged) {
                showPageIssueLayouts(page, pagingLabel, issueLayouts, issuesContainer);
            }
            previous.setEnabled(!page.isFirstPage());
            event.getSource().setEnabled(!page.isLastPage());
            event.getSource().getElement().callFunction("scrollIntoView");
        });
        previous.addClickListener(event -> {
            boolean paged = page.decrementPage();
            if (paged) {
                showPageIssueLayouts(page, pagingLabel, issueLayouts, issuesContainer);
            }
            next.setEnabled(!page.isLastPage());
            event.getSource().setEnabled(!page.isFirstPage());
            event.getSource().getElement().callFunction("scrollIntoView");
        });

        HorizontalLayout pagingLayout = new HorizontalLayout(previous, pagingLabel, next);
        pagingLayout.setDefaultVerticalComponentAlignment(Alignment.CENTER);
        pagingLayout.setMargin(true);
        add(pagingLayout);
        setHorizontalComponentAlignment(Alignment.CENTER, pagingLayout);

        previous.setEnabled(false);
        if (page.getPages() < 1) {
            next.setEnabled(false);
        }
        showPageIssueLayouts(page, pagingLabel, issueLayouts, issuesContainer);
    }

    private void showPageIssueLayouts(Page page, Label pagingLabel,
                                      List<IssueLayout> issueLayouts, VerticalLayout issuesContainer) {
        int from = page.getOffset() * page.getPageSize();
        int to = from + page.getPageSize();
        if (to > issueLayouts.size()) {
            to = issueLayouts.size();
        }
        pagingLabel.setText(page.getOffset() + " / " + page.getPages());
        issuesContainer.removeAll();
        for (IssueLayout issueLayout : issueLayouts.subList(from, to)) {
            issuesContainer.add(issueLayout);
        }

    }

    private class IssueLayout extends HorizontalLayout {

        IssueLayout(Issue issue, List<IssueComment> issueComments) {
            getElement().getStyle().set("border", "1px solid lightGrey");
            setPadding(true);
            setWidthFull();

            VerticalLayout issueLayout = new VerticalLayout();
            issueLayout.setPadding(false);
            issueLayout.setMargin(false);

            issueLayout.add(row("Issue ID", issue.getIdReadable()));
            issueLayout.add(row("Summary", issue.getSummary()));
            issueLayout.add(row("Description", issue.getDescription()));
            issueLayout.add(row("Reporter", issue.getReporter().getLogin()));
            issueLayout.add(row("Created at", new Date(issue.getCreated() * 1000).toString()));
            if (Objects.nonNull(issue.getCustomFields())) {
                for (IssueCustomField customField : issue.getCustomFields()) {
                    if (customField instanceof BaseIssueCustomField) {
                        BaseIssueCustomField<? extends BundleElement> baseIssueCustomField
                                = (BaseIssueCustomField<? extends BundleElement>) customField;
                        issueLayout.add(row(StringUtils.capitalize(customField.getProjectCustomField()
                                .getCustomField().getName()), baseIssueCustomField.getValue().getName()));
                    } else if (customField instanceof SingleUserIssueCustomField) {
                        SingleUserIssueCustomField userIssueCustomField = (SingleUserIssueCustomField) customField;
                        issueLayout.add(row(StringUtils.capitalize(customField.getProjectCustomField()
                                .getCustomField().getName()), userIssueCustomField.getValue() == null ?
                                userIssueCustomField.getProjectCustomField().getEmptyFieldText() :
                                userIssueCustomField.getValue().getLogin()));
                    } else {
                        throw new IllegalStateException("Unexpected IssueCustomField subtype = "
                                + customField.getClass().getName());
                    }
                }
            }

            VerticalLayout commentsLayout = new VerticalLayout();
            commentsLayout.setPadding(false);
            commentsLayout.setMargin(false);
            commentsLayout.add(captionLabel("Comments"));
            commentsLayout.getElement().getStyle().set("padding-left", "50px");

            if (Objects.isNull(issueComments) || issueComments.isEmpty()) {
                commentsLayout.add(new Label("No comments"));
            } else {
                Iterator<IssueComment> iterator = issueComments.iterator();
                while (iterator.hasNext()) {
                    IssueComment issueComment = iterator.next();
                    commentsLayout.add(new CommentLayout(issueComment));
                    if (iterator.hasNext()) {
                        add(new Hr());
                    }
                }
            }

            add(issueLayout);
            add(commentsLayout);
        }

        private HorizontalLayout row(String caption, String value) {
            HorizontalLayout layout = new HorizontalLayout();
            Label valueLabel = new Label(value);
            valueLabel.setWidth("400px");
            valueLabel.getElement().getStyle().set("word-break", "break-all");
            valueLabel.getElement().getStyle().set("white-space", "break-all");
            layout.add(captionLabel(caption), valueLabel);
            return layout;
        }

        private Label captionLabel(String caption) {
            Label captionLabel = new Label(caption);
            captionLabel.getElement().getStyle().set("font-weight", "bold");
            captionLabel.setWidth("200px");
            return captionLabel;
        }

    }

    private class CommentLayout extends VerticalLayout {

        CommentLayout(IssueComment issueComment) {
            setPadding(false);
            setMargin(false);

            Label textCaptionLabel = captionLabel("Text");
            Label textLabel = new Label(issueComment.getText());
            textLabel.getElement().getStyle().set("word-break", "break-all");
            if (Objects.nonNull(issueComment.getAuthor())) {
                Label authorCaptionLabel = captionLabel("Author");
                Label authorLabel = new Label(issueComment.getAuthor().getLogin());
                Label createdAtCaptionLabel = captionLabel("Created at");
                Label createdAtLabel = new Label(new Date(issueComment.getCreated() * 1000).toString());
                HorizontalLayout layout = new HorizontalLayout(slot(authorCaptionLabel, authorLabel),
                        slot(createdAtCaptionLabel, createdAtLabel));
                layout.setPadding(false);
                layout.setMargin(false);
                layout.setWidthFull();
                add(layout);
            }
            add(slot(textCaptionLabel, textLabel));
        }

        private Label captionLabel(String caption) {
            Label captionLabel = new Label(caption);
            captionLabel.getElement().getStyle().set("font-weight", "bold");
            return captionLabel;
        }

        private VerticalLayout slot(Label captionLabel, Label valueLabel) {
            VerticalLayout layout = new VerticalLayout(captionLabel, valueLabel);
            layout.setPadding(false);
            layout.setMargin(false);
            return layout;
        }

    }

    @Getter
    private class Page {

        int pages;
        int pageSize;
        int offset = 0;

        Page(int recordsSize, int pageSize) {
            pages = (int) Math.ceil(recordsSize / pageSize);
            this.pageSize = pageSize;
        }

        boolean incrementPage() {
            if (offset < pages) {
                offset++;
                return true;
            }
            return false;
        }

        boolean decrementPage() {
            if (offset > 0) {
                offset--;
                return true;
            }
            return false;
        }

        boolean isLastPage() {
            return offset == pages;
        }

        boolean isFirstPage() {
            return offset == 0;
        }

    }

}
