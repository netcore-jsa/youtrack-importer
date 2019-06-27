package software.netcore.youtrack.ui.wizard.view;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H3;
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
import software.netcore.youtrack.buisness.client.entity.field.issue.IssueCustomField;
import software.netcore.youtrack.buisness.service.youtrack.YouTrackService;
import software.netcore.youtrack.buisness.service.youtrack.entity.TranslatedIssues;
import software.netcore.youtrack.ui.wizard.conf.WizardFlow;
import software.netcore.youtrack.ui.wizard.conf.YouTrackImporterStorage;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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
        setMargin(false);
        setPadding(false);
        contentContainer.setMargin(false);

        add(new H3("Review & issues import"));
        add(contentContainer);

        if (hasStoredConfig()) {
            showTranslatedIssues();
        } else {
            translateCsvToYouTrackIssues();
        }
    }

    private void translateCsvToYouTrackIssues() {
        contentContainer.removeAll();

        contentContainer.add("Translating issues");
        ProgressBar progressBar = new ProgressBar();
        progressBar.setIndeterminate(true);
        contentContainer.add(progressBar);

        ListenableFuture<TranslatedIssues> future = service.createIssuesFromMapping(
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
        contentContainer.add(new Label("Reason = " + (ex.getMessage() == null ? ex.getClass().getName() : ex.getMessage())));
        contentContainer.add(new Button("Retry", event -> translateCsvToYouTrackIssues()));
    }

    private void showTranslatedIssues() {
        contentContainer.removeAll();

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
        if (page.getPages() <= 1) {
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

    private class IssueLayout extends VerticalLayout {

        IssueLayout(Issue issue, List<IssueComment> issueComments) {
            getElement().getStyle().set("border", "1px solid lightGrey");

            add(row("Issue ID", issue.getIdReadable()));
            add(row("Summary", issue.getSummary()));
            add(row("Description", issue.getDescription()));
            add(row("Reporter", issue.getReporter().getLogin()));
            if (Objects.nonNull(issue.getCustomFields())) {
                for (IssueCustomField customField : issue.getCustomFields()) {
                    add(row(StringUtils.capitalize(customField.getProjectCustomField().getCustomField().getName()),
                            customField.getValue().getName()));
                }
            }
        }

        private HorizontalLayout row(String caption, String value) {
            HorizontalLayout layout = new HorizontalLayout();
            Label captionLabel = new Label(caption);
            captionLabel.getElement().getStyle().set("font-weight", "bold");
            captionLabel.setWidth("200px");
            Label valueLabel = new Label(value);
            valueLabel.setWidth("400px");
            layout.add(captionLabel, valueLabel);
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
