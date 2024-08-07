<!DOCTYPE html>
<html lang="it">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>WebMarket - Home</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body>
    <div class="container mt-4">
        <h1>Benvenuto su WebMarket</h1>
        <h2>Categorie di prodotti:</h2>

        <div class="row">
            <div class="col-md-8">
                <#list categories as category>
                    <div class="card mb-3">
                        <div class="card-header">
                            ${category.name}
                        </div>
                        <div class="card-body">
                            <h5 class="card-title">Sottocategorie:</h5>
                            <#if category.getSubcategoriesWithCharacteristics()??>
                                <@renderSubcategories subcategories=category.getSubcategoriesWithCharacteristics()/>
                            <#else>
                                <p>Nessuna sottocategoria disponibile.</p>
                            </#if>
                        </div>
                    </div>
                </#list>
            </div>
        </div>
    </div>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>

<#macro renderSubcategories subcategories>
    <ul>
        <#list subcategories as subcategory, characteristics>
            <li>
                ${subcategory}
                <ul>
                    <#list characteristics as characteristic, value>
                        <li>${characteristic}: ${value}</li>
                    </#list>
                </ul>
            </li>
        </#list>
    </ul>
</#macro>